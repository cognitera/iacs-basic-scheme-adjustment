package gr.cognitera.util.base;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;



public final class Util {

    private Util() {
    }

    public static final long now() {
        return System.currentTimeMillis();
    }
	
    public static final int nowSSE() { // alias
        return secondsSinceTheEpoch();
    }

    // Year 2038 is too far to worry about ...
    public static final int secondsSinceTheEpoch() {
        return castLongToInt( System.currentTimeMillis() / 1000l );
    }

    public static final int castFloatToInt(final float f) {
        if (!canBeCastToIntWithMinorLossOfPrecision(f))
            throw new IllegalArgumentException(String.format("%.2f can't be cast to int with only minor loss of precision", f));
        else return (int) f;
    }

    public static final boolean canBeCastToIntWithMinorLossOfPrecision(final float f) {
        return !((f > Integer.MAX_VALUE) || (f < Integer.MIN_VALUE));
    }

    public static final long castFloatToLong(final float f) {
        if (!canBeCastToLongWithMinorLossOfPrecision(f))
            throw new IllegalArgumentException(String.format("%.2f can't be cast to long with only minor loss of precision", f));
        else return (long) f;
    }

    public static final boolean canBeCastToLongWithMinorLossOfPrecision(final float f) {
        return !((f > Long.MAX_VALUE) || (f < Long.MIN_VALUE));
    }


    public static final long castDoubleToLong(final double d) {
        if (!canBeCastToLongWithMinorLossOfPrecision(d))
            throw new IllegalArgumentException(String.format("%.2f can't be cast to long with only minor loss of precision", d));
        else return (long) d;
    }

    public static final boolean canBeCastToLongWithMinorLossOfPrecision(final double d) {
        return !((d > Long.MAX_VALUE) || (d < Long.MIN_VALUE));
    }
    

    public static final int castLongToInt(long l) {
        if (!canBeSafelyCastToInt(l))
            throw new IllegalArgumentException(String.format("%d can't be cast to int", l));
        else return (int) l;
    }

    public static final boolean canBeSafelyCastToInt(final long l) {
        return !((l > Integer.MAX_VALUE) || (l < Integer.MIN_VALUE));
    }

    public static final int castDoubleToInt(final double d) {
        if (!canBeSafelyCastToInt(d))
            throw new IllegalArgumentException(String.format("%f can't be cast to int", d));
        else return (int) d;
    }


    /**
     *  Returns true if the double value is a member of the mathematical set Z
     *  <p>
     *  Note that we don't say "if the double value is an Integer" to avoid confusion
     *  with the Java integer type which is but a tiny subset of Z. E.g. a huge double such
     *  as 1e14 is a member of Z and a mathematical integer, but cannot be represented
     *  as a Java int or Integer.
     *
     *  @param d  a double
     *  @return true if the double is a member of the mathematical set Z, false otherwise
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value="FE_FLOATING_POINT_EQUALITY",
        justification="Math.floor returns a float that's precisely a member of Z, and "+
                      "members of Z are represented as floats without loss of precision")
    public static final boolean fpIsInTheSetZ(final double d) {
        return (d == Math.floor(d)) && !Double.isInfinite(d);
    }

    public static final boolean fpIsWithinIntegerRange(final double d) {
        return (d >= Integer.MIN_VALUE) && (d <= Integer.MAX_VALUE);
    }
    
    public static final boolean canBeSafelyCastToInt(final double d) {
        return fpIsInTheSetZ(d) && fpIsWithinIntegerRange(d);
    }

    @SafeVarargs    
    public static final <T extends Comparable<T>> T min(final T ...ns) {
        Assert.assertTrue(ns.length>=1);
        T rv = ns[0];
        for (T n: ns) {
            rv = n.compareTo(rv) < 0 ? n : rv;
        }
        return rv;
    }

    @SafeVarargs
    public static final <T> boolean oneOf(final T y, final T...xs) {
        for (T x: xs)
            if (Objects.equals(y, x))
                return true;
        return false;
    }

    @SafeVarargs
    public static final <T> boolean areAllNull(final T...xs) {
        for (T x: xs)
            if (x!=null)
                return false;
        return true;
    }

    @SafeVarargs
    public static final <T> boolean isAnyNull(final T...xs) {
        for (T x: xs)
            if (x==null)
                return true;
        return false;
    }

    @SafeVarargs
    public static final <T> boolean noneIsNull(final T...xs) {
        return !isAnyNull(xs);
    }

    @SafeVarargs
    public static final <T> boolean allAreNullOrNoneIsNull(final T...xs) {
        return areAllNull(xs) || noneIsNull(xs);
    }

    @SafeVarargs
    public static final <T> boolean exactlyOneIsNotNull(final T...xs) {
        int howManyNotNull = 0;
        for (T x: xs) {
            if (x!=null)
                howManyNotNull++;
            if (howManyNotNull > 1)
                return false;
        }
        return howManyNotNull==1;
    }

    /*
     * floating point equality test (for a given epsilon value)
     */
    public static boolean fpEqual(final double a, final double b, final double epsilon) {
        return Math.abs(a-b)<=epsilon;
    }

    
    
}
