package gr.cognitera.iacs.basic_scheme_adjustment;

import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public class Right {

    public long id;
    public long internal_id;

    public RightSource source;
    public RightType type;
    public BigDecimal quantity;
    public BigDecimal unit_value;

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    public Right() {}

    public Right(
                 final long id,
                 final long internal_id,
                 final RightSource source,
                 final RightType type,
                 final BigDecimal quantity,
                 final BigDecimal unit_value
                 ) {
        this.id = id;
        this.internal_id = internal_id;
        this.source = source;
        this.type = type;
        this.quantity = quantity;
        this.unit_value = unit_value;
    }

    public Right(final Right x) {
        this(x.id
             , x.internal_id
             , x.source
             , x.type
             , x.quantity
             , x.unit_value);
    }
            

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("internal_id", internal_id)
            .add("source", source)
            .add("type", type)
            .add("quantity", quantity)
            .add("unit_value", unit_value)
            ;
    }


    public static List<Right> copy(final List<Right> rs) {

        final List<Right> rv = new ArrayList<>();
        for (final Right r: rs) {
            rv.add(new Right(r));
        }
        return rv;
    }

    public BigDecimal totalValue() {
        /*
          The following query returns no rows:
          select e.bpeqty, e.bpeup, e.bpevalue, e.bpevalue - round(e.bpeqty * e.bpeup, 2) from gaee2020.EDETEDEAEEBPE e 
          where e.BPEVALUE <> round(e.bpeqty * e.bpeup, 2)

          [round] in Oracle rounds to the nearest, and in case of LSB 5, it rounds away from zero.
          Hence the corresponding rounding mode in Java is HALF_UP
        */
        return Util.mulWithScale(this.quantity, this.unit_value, 2, RoundingMode.HALF_UP);
    }

    public static RightStats computeStats(final List<Right> rs) {
        return computeStats(rs, (RightType) null);
    }
    
    public static RightStats computeStats(final List<Right> rs
                                        , final RightType rightType) {
        return computeStats(rs, rightType, RightValueSelector.ALL, null);
    }

    public static RightStats computeStats(final List<Right> rs
                                          , final RightType rightType
                                          , final RightValueSelector selector
                                          , final BigDecimal x) {
        int numOfRecords = 0;
        BigDecimal numOfRights      = BigDecimal.ZERO;
        BigDecimal valueOfRights    = BigDecimal.ZERO;
        BigDecimal lowestUnitValue  = null;
        BigDecimal highestUnitValue = null;
        for (final Right r: rs) {
            if ((rightType==null) || (r.type.equals(rightType))) {
                final boolean consider =
                    selector.equals(RightValueSelector.ALL) ||
                    (
                     (selector.equals(RightValueSelector.ABOVE) && (r.unit_value.compareTo(x)>0))
                     ||
                     (selector.equals(RightValueSelector.EXACT) && (r.unit_value.compareTo(x)==0))
                     ||
                     (selector.equals(RightValueSelector.BELOW) && (r.unit_value.compareTo(x)<0))
                     );
                if (consider) {
                    numOfRecords++;
                    numOfRights = numOfRights.add(r.quantity);
                    valueOfRights = valueOfRights.add(r.totalValue());
                    if ((lowestUnitValue==null) || (r.unit_value.compareTo(lowestUnitValue)<0))
                        lowestUnitValue = r.unit_value;
                    if ((highestUnitValue==null) || (r.unit_value.compareTo(highestUnitValue)>0))
                        highestUnitValue = r.unit_value;
                }
            }
        }
        return new RightStats(numOfRecords
                              , numOfRights
                              , valueOfRights
                              , lowestUnitValue
                              , highestUnitValue);
    }
    

}




