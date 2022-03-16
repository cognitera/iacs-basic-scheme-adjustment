package gr.cognitera.iacs.basic_scheme_adjustment;

public enum RightType {

    PASTURE(1), ARABLE(2), PERMACROP(3);

    private int code;

    private RightType(final int code) {
        this.code = code;
    }

    public static RightType fromCode(final int code) {
        for (RightType x: RightType.values())
            if (x.code == code)
                return x;
        return null;
    }
    
}

    
