package gr.cognitera.iacs.basic_scheme_adjustment;

import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;


import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public class Right {

    public long id;
    public long internal_id;

    public RightSource source;
    public RightType type;
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
                 final BigDecimal unit_value
                 ) {
        this.id = id;
        this.internal_id = internal_id;
        this.source = source;
        this.type = type;
        this.unit_value = unit_value;
    }

    public Right(final Right x) {
        this(x.id, x.internal_id, x.source, x.type, x.unit_value);
    }
            

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("internal_id", internal_id)
            .add("source", source)
            .add("type", type)
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

}




