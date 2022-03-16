package gr.cognitera.iacs.basic_scheme_adjustment;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;


import java.math.BigDecimal;

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

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("internal_id", internal_id)
            .add("source", source)
            .add("type", type)
            .add("unit_value", unit_value)
            ;
    }
}




