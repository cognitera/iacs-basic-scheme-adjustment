package gr.cognitera.iacs.basic_scheme_adjustment;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public enum RightType {

    PASTURE(1, "pasture"), ARABLE(2, "arable"), PERMACROP(3, "permacrop");

    private final int code;
    public final String name;

    private RightType(final int code
                      , final String name) {
        this.code = code;
        this.name = name;
    }

    public static RightType fromCode(final int code) {
        for (RightType x: RightType.values())
            if (x.code == code)
                return x;
        return null;
    }

    
    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("code", code)
            ;
    }
    




    
}

    
