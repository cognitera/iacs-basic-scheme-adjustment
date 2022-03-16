package gr.cognitera.iacs.basic_scheme_adjustment;

public enum RightSource {

    OWN(1), NATIONAL_BANK(2);
        
    private int code;

    private RightSource(final int code) {
        this.code = code;
    }

    public static RightSource fromCode(final int code) {
        for (RightSource x: RightSource.values())
            if (x.code == code)
                return x;
        return null;
    }

}
