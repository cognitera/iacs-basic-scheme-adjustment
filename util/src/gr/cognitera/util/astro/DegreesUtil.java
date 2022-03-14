package gr.cognitera.util.astro;

import org.junit.Assert;

import gr.cognitera.util.base.Util;


public final class DegreesUtil {

    private DegreesUtil() {}

    public static String formatRAandDEC_as_HMS_DMS(final double ra, final double dec) {
        return String.format("%s %s"
                             , rightAscensionToHourMeasure( ra, 3)
                             , declinationToDegreesMeasure(dec, 3));
    }

    public static String rightAscensionToHourMeasure(final double degrees
                                                     , final int secondsPrecision) {
        if ((degrees < 0) || (degrees > 360))
            throw new IllegalArgumentException(String.format("impossible degree value: [%6.3f]"
                                                             , degrees));
        if (secondsPrecision < 0)
            throw new IllegalArgumentException(String.format("impossible seconds precision value: [%d]"
                                                             , secondsPrecision));
        double residualSeconds = (degrees / 360) * (24*3600);
        final int wholeHours = Util.castDoubleToInt(Math.floor(residualSeconds / (60*60)));
        Assert.assertTrue(String.format("impossible whole hours value (%d) derived from degrees value of %10.5f"
                                        , wholeHours
                                        , degrees)
                          , (wholeHours>=0) && (wholeHours < 24));


        residualSeconds -= wholeHours * (60*60);
        final int wholeMinutes = Util.castDoubleToInt(Math.floor(residualSeconds / 60));
        Assert.assertTrue(String.format("impossible whole minutes value (%d) derived from degrees value of %10.5f"
                                        , wholeMinutes
                                        , degrees)
                          , (wholeMinutes>=0) && (wholeMinutes < 60));

        residualSeconds -= wholeMinutes * (60);

        Assert.assertTrue(String.format("impossible final residual seconds value (%6.3f) derived from degrees value of %10.5f"
                                        , residualSeconds
                                        , degrees)
                          , (residualSeconds>=0) && (residualSeconds < 60));
        return format(wholeHours, wholeMinutes, residualSeconds, secondsPrecision);
    }

    public static String declinationToDegreesMeasure(final double degrees
                                                     , final int secondsPrecision) {
        final String valueWithoutSign = absoluteDeclinationToDegreesMeasure(Math.abs(degrees), secondsPrecision);
        return String.format("%s%s", degrees>=0?"+":"-", valueWithoutSign);
    }

    public static String absoluteDeclinationToDegreesMeasure(final double degrees
                                                             , final int secondsPrecision) {
        if ((degrees < 0) || (degrees > 90))
            throw new IllegalArgumentException(String.format("impossible degree value: [%6.3f]"
                                                             , degrees));
        if (secondsPrecision < 0)
            throw new IllegalArgumentException(String.format("impossible seconds precision value: [%d]"
                                                             , secondsPrecision));
            
        double residualSeconds = degrees*60*60;
        final int wholeDegrees = Util.castDoubleToInt(Math.floor(residualSeconds / (60*60)));
        Assert.assertTrue(String.format("impossible whole degrees value (%d) derived from degrees value of %10.5f"
                                        , wholeDegrees
                                        , degrees)
                          , (wholeDegrees>=0) && (wholeDegrees <= 90));


        residualSeconds -= wholeDegrees * (60*60);
        final int wholeMinutes = Util.castDoubleToInt(Math.floor(residualSeconds / 60));
        Assert.assertTrue(String.format("impossible whole minutes value (%d) derived from degrees value of %10.5f"
                                        , wholeMinutes
                                        , degrees)
                          , (wholeMinutes>=0) && (wholeMinutes < 60));

        residualSeconds -= wholeMinutes * (60);

        Assert.assertTrue(String.format("impossible final residual seconds value (%6.3f) derived from degrees value of %10.5f"
                                        , residualSeconds
                                        , degrees)
                          , (residualSeconds>=0) && (residualSeconds < 60));
        return format(wholeDegrees, wholeMinutes, residualSeconds, secondsPrecision);
    }

    private static String format(int hoursOrDegrees, int m, double s, int secondsPrecision) {
        final String secondsMask = String.format("%%0%d.%df"
                                                 , secondsPrecision + 2 + ((secondsPrecision>0)?1:0)
                                                 , secondsPrecision);

        return String.format("%s %s %s"
                             , padHoursOrMinutesWithZeros(hoursOrDegrees)
                             , padHoursOrMinutesWithZeros(m)
                             , String.format(secondsMask, s));
    }
    

    private static String padHoursOrMinutesWithZeros(int n) {
        Assert.assertTrue(String.format("value of [%d] is more than 2 characters wide"
                                        , n)
                          , String.valueOf(n).length() <= 2);
        return String.format("%02d", n);
    }
}
