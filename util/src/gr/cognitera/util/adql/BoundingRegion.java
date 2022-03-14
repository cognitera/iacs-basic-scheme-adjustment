package gr.cognitera.util.adql;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.Range;

import gr.cognitera.util.base.Maybe;

public class BoundingRegion {


    public final Maybe<ContiguousRAConstraint> ra;
    public final Maybe<Range<Double>> dec;

    private BoundingRegion(final Maybe<ContiguousRAConstraint> ra
                          , final Maybe<Range<Double>> dec) {
        this.ra  = ra;
        this.dec = dec;
    }


    public static BoundingRegion decConstraintOnly(Maybe<Range<Double>> dec) {
        return new BoundingRegion(Maybe.of( (ContiguousRAConstraint) null)
                                  , dec);
    }

    public static BoundingRegion of(Maybe<ContiguousRAConstraint> ra
                                    , Maybe<Range<Double>> dec) {
        return new BoundingRegion(ra, dec);
    }

    public String adqlCondition(final String raColumn, final String decColumn) {
        return String.format("(%s) AND (%s)"
                             , ra.isPresent ()?ra .value.adqlCondition (raColumn):"1=1"
                             , dec.isPresent()?RangeUtil.rangeCondition(decColumn, dec.value):"1=1");
    }


    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("ra", ra)
            .add("dec", dec)
            ;
    }
    
}
