package gr.cognitera.util.adql;

import org.junit.Assert;

import com.google.common.collect.Range;
import com.google.common.collect.BoundType;


import gr.cognitera.util.base.Util;
import gr.cognitera.util.base.Pair;
import gr.cognitera.util.base.SCAUtils;
import gr.cognitera.util.base.Maybe;


public class BoundingRegionCalculator {

    private static double EPSILON = 1E-6;

    public static final Range<Double> ZERO_TO_360    = Range.range(  0.0, BoundType.CLOSED, 360.0, BoundType.CLOSED);
    public static final Range<Double> MINUS_90_TO_90 = Range.range(-90.0, BoundType.CLOSED,  90.0, BoundType.CLOSED);
    public static final Range<Double> ZERO_TO_180    = Range.range(  0.0, BoundType.CLOSED, 180.0, BoundType.CLOSED);

    static {
        Assert.assertEquals((Double) ZERO_TO_360   .lowerEndpoint(), (Double)    0.0 );
        Assert.assertEquals((Double) ZERO_TO_360   .upperEndpoint(), (Double)  360.0 );
        Assert.assertEquals((Double) MINUS_90_TO_90.lowerEndpoint(), (Double) (-90.0));
        Assert.assertEquals((Double) MINUS_90_TO_90.upperEndpoint(), (Double)   90.0 );
        Assert.assertEquals((Double) ZERO_TO_180   .lowerEndpoint(), (Double)    0.0 );
        Assert.assertEquals((Double) ZERO_TO_180   .upperEndpoint(), (Double)  180.0 );
    }


    public static BoundingRegion calculate(double ra, double dec, double sr) {
        check("RA" , ra , ZERO_TO_360);
        check("DEC", dec, MINUS_90_TO_90);
        check("SR" , sr , ZERO_TO_180);
        final boolean weGoAboveTheNorthOrBelowTheSouthPole = (dec-sr<-90) || (dec+sr > 90);
        if (weGoAboveTheNorthOrBelowTheSouthPole) {
            // in these two cases we have no constraint at all on the RA
            return BoundingRegion.decConstraintOnly(decConstraint(dec, sr));
        } else {
            Assert.assertFalse((sr == 90) || (sr == -90));

            // this is the horizontal (RA) projection of the SR on the celestial equator
            double raProjectionOfSRAtCelestialEquator = sr / Math.cos(Math.toRadians(dec));
            if (raProjectionOfSRAtCelestialEquator>=180) {
                // in this case we also have no constraint at all on the RA
                return BoundingRegion.decConstraintOnly(decConstraint(dec, sr));
            } else {
                check("SR", raProjectionOfSRAtCelestialEquator, ZERO_TO_180);
                return BoundingRegion.of(raConstraint(ra, raProjectionOfSRAtCelestialEquator), decConstraint(dec, sr));
            }
        }
    }

    private static void check(final String name, final double v, final Range<Double> range) {
        if (!range.contains(v))
            throw new IllegalArgumentException(String.format("%s has to be in %s, yet it was: [%9.5f]"
                                                             , name
                                                             , ZERO_TO_360.toString()
                                                             , v));
    }

    protected static Maybe<ContiguousRAConstraint> raConstraint(final double ra, final double sr) {
        check("RA", ra, ZERO_TO_360);
        check("SR", sr, ZERO_TO_180);
        final double raLow = ra-sr;
        Assert.assertTrue(String.format("impossible value for raLow=[%f]", raLow), (raLow>=-180) && (raLow<=360));
        
        final double raHi  = ra+sr;
        Assert.assertTrue(String.format("impossible value for raHi=[%f]", raHi), (raHi>=0) && (raHi<=360+180));

        Assert.assertFalse(String.format("impossible value for [raLow, raHi] = [%f, %f]", raLow, raHi)
                           , raHi-raLow>2*180);

        // first we deal with the edge cases that translate to no constraint at all

        // edge case #1: any time the [sr] equals 180, then regardless of the [ra] we effectively have no constraint at all on the RA
        if (sr==180)
            return new Maybe<ContiguousRAConstraint>(null);
        
        // [0 ... raLow ... raHi ... 360]  ---> [raLow...raHi]
        if (ZERO_TO_360.contains(raLow) && ZERO_TO_360.contains(raHi) && raLow <= raHi) {
            Assert.assertTrue((sr > 0) || Util.fpEqual(raLow, raHi, EPSILON));
            return Maybe.of(ContiguousRAConstraint.singleBand(Range.range(raLow, BoundType.CLOSED, raHi, BoundType.CLOSED)));
        }

        // [raLow ... 0 ... raHi ... 360] ---> [360+raLow...360] AND [0...raHi]
        if (ZERO_TO_360.contains(raHi) && (raLow < ZERO_TO_360.lowerEndpoint())) {
            final Range<Double> a = Range.atLeast(360+raLow).intersection(ZERO_TO_360);
            final Range<Double> b = Range.atMost(raHi).intersection(ZERO_TO_360);
            return Maybe.of(ContiguousRAConstraint.dualBand(a, b));
        }

        // [0 ... raLow ... 360 ... raHi] ---> [raLow...360] AND [0...raHi]
        if (ZERO_TO_360.contains(raLow) && (raHi > ZERO_TO_360.upperEndpoint())) {
            final Range<Double> a = Range.atLeast(raLow).intersection(ZERO_TO_360);
            final Range<Double> b = Range.atMost(raHi-360.0).intersection(ZERO_TO_360);
            return Maybe.of(ContiguousRAConstraint.dualBand(a, b));
        }
        return failIfWeEverReachThisLine();
    }

    @SuppressWarnings("unchecked")
    private static Maybe<ContiguousRAConstraint> failIfWeEverReachThisLine() {
        return (Maybe<ContiguousRAConstraint>) SCAUtils.CANT_REACH_THIS_LINE(Maybe.class);
    }

    protected static Maybe<Range<Double>> decConstraint(final double dec, final double sr) {
        check("DEC", dec, MINUS_90_TO_90);
        check("SR" , sr , ZERO_TO_180);
        if (sr==180) {
            // if the search radius is equal to 180 then, regardless of the declination, we effectively have no constraint at all on the DEC
            return Maybe.of( (Range<Double>) null);
        }
        final boolean weGoAboveTheNorthPole = dec+sr >  90;
        final boolean weGoBelowTheSouthPole = dec-sr < -90;
        if (weGoAboveTheNorthPole && weGoBelowTheSouthPole) {
            // if we go above both Poles then there is no constraint on declination
            return Maybe.of( (Range<Double>) null);
        } else if (weGoAboveTheNorthPole) {
            return Maybe.of(Range.atLeast(dec-sr).intersection(MINUS_90_TO_90));
        } else if (weGoBelowTheSouthPole) {
            return Maybe.of(Range.atMost(dec+sr).intersection(MINUS_90_TO_90));
        } else {
            Assert.assertTrue(MINUS_90_TO_90.contains(dec-sr));
            Assert.assertTrue(MINUS_90_TO_90.contains(dec+sr));
            return Maybe.of(Range.range(dec-sr, BoundType.CLOSED, dec+sr, BoundType.CLOSED));
        }
    }

}
