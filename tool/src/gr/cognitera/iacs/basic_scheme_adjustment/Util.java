package gr.cognitera.iacs.basic_scheme_adjustment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.MathContext;


public final class Util {

    private Util() {}

    public static BigDecimal mulWithScale(final BigDecimal a
                                          , final BigDecimal b
                                          , final int scale
                                          , final RoundingMode rounding) {
        final BigDecimal rv = a.multiply(b, MathContext.UNLIMITED);
        final BigDecimal rv2 = rv.setScale(scale, rounding);
        return rv2;
    }
}
