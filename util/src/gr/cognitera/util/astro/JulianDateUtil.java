package gr.cognitera.util.astro;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;

import gr.cognitera.util.base.Util;

import gr.cognitera.util.time.YearMonthDay;
import gr.cognitera.util.time.YearMonthDayHourMinSecMil;
import gr.cognitera.util.time.DateUtil;



/**
 *  Utilities for converting from Julian dates, and modified Julian dates to 
 *  <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Date.html">java.util.Date</a>
 *  <p>
 *  If you are just interested in converting Julian Dates or Modified Julian Dates to 
 *  <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Date.html">java.util.Date</a> values
 *  then you only need to use the following two methods (respectively):
 *  <ul>
 *      <li>{@link #jDToDate(double) jDToDate}</li>
 *      <li>{@link #mJDToDate(double) mJDToDate}</li>
 *  </ul>
 *  <p>
 *  The reverse conversion (from date to Julian Date) is also available in
 *  {@link #dateToJulianDate(Date) dateToJulianDate}.
 *  <p>
 *  If you just want to perform the above conversions you may stop reading. Otherwise if you wish to understand the 
 *  implementation, read on &hellip;
 *  <p>
 *  Note that this is a subject that can run <b>very</b> deep. Be sure to read some
 *  authoritative source and note that the following concepts are distinct:
 *  <ul>
 *    <li>Julian Day and Julian Day Number</li>
 *    <li>Julian Date</li>
 *    <li>Modified Julian Date</li>
 *  </ul>
 *  <p>
 *      It also helps if you are aware of the differences between Universal Time, GMT, UT1, UTC and TAI.
 *      This <a href='https://physics.stackexchange.com/a/440607/218859'>physics.stackexchange answer</a>
 *      explains the different nuances quite well. Since Julian Dates are used for astronomical computations and observations
 *      we want to use <i>UT1</i> and not <i>UTC</i>.
 *  </p>
 *  <p>
 *  In addition to the above, to understand the implementation you should be familiar with the 
 *  standard time-related classes in Java 7 and understand e.g. the concept of a proleptic
 *  Gregorian or Julian calendar and also be aware that the 
 *  <a href="https://docs.oracle.com/javase/7/docs/api/java/util/GregorianCalendar.html">java.util.GregorianCalender</a>
 *  class in Java is, despite its name, a hybrid calendar that supports both Gregorian and Julian calendars.
 *  <p>
 *  The test cases for this class have been written using, among other sources, conversion values obtained from the 
 *  <a href="https://aa.usno.navy.mil/data/docs/JulianDate.php" target="_blank">US Naval Observatory Julian Date converter page</a>
 *  and the <a href="https://heasarc.gsfc.nasa.gov/cgi-bin/Tools/xTime/xTime.pl">NASA HEASARC time converter utility</a>.
 *  <p>
 *  Some other tutorial-level material on time units is given in the links below:
 *  <ul>
 *    <li><a href="http://cxc.harvard.edu/contrib/arots/time/time_tutorial.html#systems">Chandra tutorial on time systems</a></li>
 *    <li><a href="https://en.wikipedia.org/wiki/Julian_day#Variants">Julian Day variants</a></li>
 *    <li><a href="http://scienceworld.wolfram.com/astronomy/JulianDate.html">Julian Date article on Wolfram</a></li>
 *    <li><a href="http://scienceworld.wolfram.com/astronomy/ModifiedJulianDate.html">Modified Julian Date article on Wolfram</a></li>
 *    <li><a href="http://www.astro.sunysb.edu/fwalter/PHY515/times.html">just another time units tutorial page</a></li>
 *  </ul>
 */

public final class JulianDateUtil {

    /**
     * Time zone for reckoning Julian dates.
     * <p>
     * Julian dates are based on Universal Time; hence
     * you want to use UT1 which is a time standard based on Earth's rotation, i.e. the Mean
     * Solar Time on the Prime Meridian, and not UTC. UT1 is the principal form of Universal Time.
     * UTC is synchronized with TAI (International Atomic Time) and differs from it by an integral
     *  number of seconds (leap seconds which are introduced to UTC to keep the difference between
     * UT1 and UTC less than 0.9s).
     * <p>
     * In practical terms it makes no sense to dwell upon the different
     * nuances between UT1 and UTC as both Windows and Unix systems (and the Network Time Protocol)
     * handle leap seconds wrong. Java also, I suspect, does not faithfully reflect the differences
     * between UT1 and UTC. E.g. the unit test cases for this class also succeed if we use UTC
     * for this field instead of UT1. Finally, Java
     *  <a href='https://stackoverflow.com/a/35465689/274677'>does not properly support leap seconds either</a>
     * further confounding the difference between UT1 and UTC.
     *
     */
    public static final TimeZone TZ_FOR_RECKONING_JULIAN_DATES = TimeZone.getTimeZone("UT1");
    
    private JulianDateUtil() {}

    private static void assert_non_negative_double(final double d, final String paramName) {
        if (d < 0)
            throw new IllegalArgumentException(String.format("negative value (%f) was passed for parameter [%s]"
                                                             , d
                                                             , paramName));
    }

    private static void assert_non_negative_int(final int i, final String paramName) {
        if (i < 0)
            throw new IllegalArgumentException(String.format("negative value (%d) was passed for parameter [%s]"
                                                             , i
                                                             , paramName));
    }

    private static long daysToMillis(final double days) {
        assert_non_negative_double(days, "days");
        final long msInADay = TimeUnit.DAYS.toMillis(1);
        final double rv = days*msInADay;
        return Util.castDoubleToLong(rv);
    }

    /**
     * Converts a Julian Date to a specific point in time.
     * <p>
     * Julian Dates are reckoned on the Universal Time time zone (i.e. UT1 in Java, not UTC).
     * This is very clearly stated in a number of authoritative sources.
     * <p>
     * The <i>United States Naval Observatory</i> (USNO) has this to say on the
     * <a href="https://aa.usno.navy.mil/data/docs/JulianDate.php" target="_blank">Julian Date Converter page</a>:
     * <blockquote>Julian dates (abbreviated JD) are simply a continuous count of days and fractions since noon Universal Time on 
     * January 1, 4713 BC (on the Julian calendar)</blockquote>
     * <p>
     * In the
     * <a href="https://tycho.usno.navy.mil/mjd.html" target="_blank">page on Modified Julian Dates</a> (again at the USNO site)
     * we read:
     * <blockquote>The MJD is always referred to as a time reckoned in Universal Time (UT)</blockquote>
     * Given the close
     * association between MJD and JD, it makes sense that the USNO uses the same reckoning locale (time zone) for both.
     * <p>
     * In the 
     * <a href="http://cxc.harvard.edu/contrib/arots/time/time_tutorial.html#systems">Chandra Data Archive Time Tutorial</a>
     * we read:
     * <blockquote>JD (Julian Date): Number of days since Greenwich mean noon on January 1, 4713 B.C.</blockquote>
     * Note that the above definition uses <i>GMT</i> and not <i>Universal Time</i> which is unfortunate as <i>GMT</i> is an obsolete
     * and not technically well-defined term. The proper unambiguous terminology to use is <i>UT1</i> (mean solar day derived
     * from astronomical measurements) versus <i>UTC</i> (time based on atomic clocks).
     * <p>
     * Finally, in the <a href="https://en.wikipedia.org/wiki/Julian_day">Wikipedia page on Julian days</a> we read:
     * <blockquote>The Julian date (JD) of any instant is the Julian day number plus the fraction of a day since the preceding noon 
     * in Universal Time</blockquote> &hellip; where the <i>Julian day number</i> has been previously defined as follows:
     * <blockquote>The Julian Day Number (JDN) is the integer assigned to a whole solar day in the Julian day count starting 
     * from noon Universal time, with Julian day number 0 assigned to the day starting at noon on Monday, January 1, 4713 BC, 
     * proleptic Julian calendar (November 24, 4714 BC, in the proleptic Gregorian calendar)</blockquote>
     * <p>
     * The implementation is based on the 
     * {@link #jDToDateAsPerJeanMeeus jDToDateAsPerJeanMeeus} method.
     *
     * @param julianDate  the Julian date
     * @return the point in time corresponding to that Julian date assuming Julian dates are reckoned on the UT1 timezone.
     */
    public static Date jDToDate(final double julianDate) {
        assert_non_negative_double(julianDate, "julianDate");
        return jDToDateAsPerJeanMeeus(julianDate);
    }

            
    /**
     * Converts a Julian Date to a specific point in time.
     * <p>
     * The implementation is based on the sister
     * {@link #jDToDateAsPerJava(java.util.TimeZone, double) jDToDateAsPerJava} method.
     *
     * @param julianDate  the Julian date
     * @return the point in time corresponding to that Julian date assuming Julian dates are reckoned on the UT1 timezone.
     *
     */
    protected static Date jDToDateAsPerJava(final double julianDate) {
        assert_non_negative_double(julianDate, "julianDate");
        return jDToDateAsPerJava(TZ_FOR_RECKONING_JULIAN_DATES, julianDate);
    }
        
    /**
     * Converts a Julian Date to a specific point in time assuming that the JDNs are reckoned
     * on the supplied time zone.
     * <p>
     * This method is not meant to be used
     * by client programmers and exists to afford more thorough testing (by varying the time zones and establishing certain invariants).
     * <p>
     * The implementation relies on Java's own date / time algorithms such as are implemented
     * in classes <a href='https://docs.oracle.com/javase/7/docs/api/java/util/Calendar.html'>Calendar</a>
     * and  <a href='https://docs.oracle.com/javase/7/docs/api/java/util/Date.html'>Date</a>
     * <p>
     * The implementation also uses the well-established definition that Julian Days are reckoned on
     * the UT1 time zone.
     * <p>
     * See also the {@link #jDToDateAsPerJava(double) overriden julianDateToDate} utility method.
     *
     * @param julianDate  the Julian date
     * @param timeZone  the time zone on which we are assuming Julian days are counted
     * @return the point in time corresponding to that Julian date
     *
     */
    protected static Date jDToDateAsPerJava(final TimeZone timeZone, final double julianDate) {
        assert_non_negative_double(julianDate, "julianDate");
        final Date startOfJulianEra = startOfJulianEraAssumingFlexibleTimeZone(timeZone);
        return new Date(startOfJulianEra.getTime()+daysToMillis(julianDate));
    }

    private static Date startOfJulianEraAssumingFlexibleTimeZone(final TimeZone timeZone) {
        final Calendar calendar = new GregorianCalendar(timeZone, Locale.ENGLISH);
        /*
         * See: https://docs.oracle.com/javase/8/docs/api/java/time/temporal/JulianFields.html
         * NB: at the time of writing of this class we're still using Java 7 so the above link is provided
         *     for documentation purposes only
         *
         * "Julian Day is a well-known system that represents the count of whole days since day 0, which 
         *  is defined to be January 1, 4713 BCE in the Julian calendar, and -4713-11-24 Gregorian."
         *
         *
         * NB2: In Java 7 the GregorianCalendar class is a hybrid class that supports both the Julian and
         *      the Gregorian calendar systems and knows the exact date when the Gregorian Calendar was
         *      introduced (in the Roman Catholic countries): October 15, 1582. As such, to define the 
         *      start of the Julian Era we directly supply the date given for the Julian calendar (January 1, 4713 BC).
         *      I.e. it would be a mistake to use the (proleptic) Gregorian calender version for that date (November 24, 4713 BC).
         *
         */
        calendar.clear();
        calendar.setLenient(false);
        calendar.set(Calendar.ERA          , GregorianCalendar.BC);
        calendar.set(Calendar.YEAR         , 4713);
        calendar.set(Calendar.MONTH        , Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH ,  1);
        calendar.set(Calendar.HOUR_OF_DAY  , 12);
        calendar.set(Calendar.MINUTE       ,  0);
        calendar.set(Calendar.SECOND       ,  0);
        calendar.set(Calendar.MILLISECOND  ,  0);
        return calendar.getTime();
    }

    protected static Date startOfJulianEra() {
        return startOfJulianEraAssumingFlexibleTimeZone(TZ_FOR_RECKONING_JULIAN_DATES);
    }
    

    /**
     * Converts a Modified Julian Date (MJD) to a Julian Date (JD)
     *
     * @param mJD  the MJD
     * @return the JD corresponding to the supplied MJD
     *
     */
    public static double mJDToJD(final double mJD) {
        assert_non_negative_double(mJD, "mJD");
        return mJD + 2400000.5;
    }


    /**
     * Converts a Modified Julian Date (MJD) to a specific point in time
     * <p>
     * The implementation is based on the
     * {@link #mJDToDateAsPerJeanMeeus mJDToDateAsPerJeanMeeus}
     * method.
     *
     * @param mJD  the MJD (assumed to be observed on the UT1 time zone)
     * @return the point in time corresponding to that MJD
     *
     * @see <a href="https://en.wikipedia.org/wiki/Julian_day#Variants">Julian Day variants</a>
     * @see <a href="https://tycho.usno.navy.mil/mjd.html" target="_blank">US Naval Observatory page on Modified Julian Dates</a>
     *
     */
    public static Date mJDToDate(final double mJD) {
        assert_non_negative_double(mJD, "mJD");
        return mJDToDateAsPerJeanMeeus(mJD);
    }

    /**
     * Converts a Modified Julian Date (MJD) to a specific point in time using Java's own algorithms
     * <p>
     * The implementation piggy-backs on the {@link #jDToDateAsPerJava jDToDateAsPerJava}
     * method that ultimately relies on the date / time algorithms of Java.
     *
     * @param mJD  the MJD
     * @return the point in time corresponding to that MJD
     *
     *
     */
    protected static Date mJDToDateAsPerJava(final double mJD) {
        assert_non_negative_double(mJD, "mJD");
        return jDToDateAsPerJava(mJDToJD(mJD));
    }

    /**
     * Converts a Modified Julian Date (MJD) to a specific point in time using Jean Meeus's algorithm
     * <p>
     * The implementation piggy-backs on the {@link #jDToDateAsPerJeanMeeus jDToDateAsPerJeanMeeus}
     *
     * @param mJD  the MJD
     * @return the point in time corresponding to that MJD
     *
     *
     */
    protected static Date mJDToDateAsPerJeanMeeus(final double mJD) {
        assert_non_negative_double(mJD, "mJD");
        return jDToDateAsPerJeanMeeus(mJDToJD(mJD));
    }
    

    /**
     * Convenience method that converts a time-zone-agnostic Julian Day Number (JDN) to an
     * (equally) time agnostic year-month-day value.
     * <p>
     * The implementation relies on Java's own date / time algorithms such as are implemented
     * in classes <a href='https://docs.oracle.com/javase/7/docs/api/java/util/Calendar.html'>Calendar</a>
     * and  <a href='https://docs.oracle.com/javase/7/docs/api/java/util/Date.html'>Date</a>
     *
     * @param julianDayNumber  the Julian Date without reference to a specific time zone
     * @return a {@link YearMonthDay YearMonthDay} value, likewise without reference to a specific time zone
     *
     */
    protected static YearMonthDay jDToCivilianAsPerJava(final int julianDayNumber) {
        assert_non_negative_int(julianDayNumber, "julianDayNumber");
        final Date date = jDToDateAsPerJava(TZ_FOR_RECKONING_JULIAN_DATES, julianDayNumber);
        return DateUtil.fieldsOnTimeZone(date, TZ_FOR_RECKONING_JULIAN_DATES).toYearMonthDay(false);
    }

    /**
     * Converts a Julian day to a calendar date
     * <p>
     * This version uses the algorithm laid out in pages 14 and 15 of
     * Numerical Recipes in C, 2nd ed., Cambridge University Press 1992
     * <p>
     * This code agrees with my implementation from some ample future day all the way back to
     * one day after the adoption of the Gregorian calendar in the Roman Catholic world 
     * (Gregorian date 15 October 1583)
     * <p>
     * A similar algorithm that agrees with my implementation over a wider range of dates
     * is given in {@link #jDToCivilianAsPerMeeusAstronomicalAlgorithms}
     * <p>
     * See JUnit test cases code for more.
     *
     * @param julianDayNumber  the Julian Day Number (JDN)
     *                         (assumed to be reckoned on UT1)
     * @return a {@link YearMonthDay YearMonthDay} value, likewise assumed to be on the UT1 time zone
     *
     */    
    public static YearMonthDay jDToCivilianAsPerNumericalRecipesInC(final int julianDayNumber) {
        assert_non_negative_int(julianDayNumber, "julianDayNumber");
        // a similar implementation of the same algorithm is found in https://www.rgagnon.com/javadetails/java-0506.html
        final int IGREG=15+31*(10+12*1582); // Gregorian Calendar adopted Oct. 15, 1582.
        int jalpha,ja,jb,jc,jd,je,year,month,day;

        if (julianDayNumber>= IGREG) {
            jalpha = (int) (((julianDayNumber - 1867216) - 0.25) / 36524.25);
            ja = julianDayNumber + 1 + jalpha - (int) (jalpha * 0.25);
        } else
            ja = julianDayNumber;

        jb = ja + 1524;
        jc = (int) (6680f + ((jb - 2439870) - 122.1) / 365.25);
        jd = (int) 365 * jc + (int) (jc * 0.25);
        je = (int) ((jb - jd) / 30.6001);
        day = jb - jd - (int) (30.6001 * je);
        month = je - 1;
        if (month > 12) month = month - 12;
        year = jc - 4715;
        if (month > 2) year--;
        if (year <= 0) year--;

        return new YearMonthDay(year, month, day);
    }

    /**
     * Converts a Julian Date value to a {@link YearMonthDayHourMinSecMil} instance
     * <p>
     * The code is adapted (with some bug fixes too) from
     * <a href='https://stackoverflow.com/q/14988459/274677' target=_blank>this SO question</a>
     * and is purpotedly based on algorithms published in Jean Meeus's "Astronomical Algorithms", 1st ed., 1991.
     * <p>
     * The code is remarkably accurate and has been tested for all <i>integer</i>, positive Julian dates from the end of the 
     * 21st centutry all the way back to Julian day 0 (UT1 Noon on 1st January 4713 BC, proleptic Julian Calendar). It has also
     * been established by testing that the values returned by this algorithm and the values returned by the Java-based implementation
     * {@link #jDToDateAsPerJava(double) jDToDateAsPerJava} are within one millisecond of each other for a wide range of 
     * contemporary and future Julian Date values (including both integer and fractional Julian Dates).
     * <p>
     * In that sense, this method is much better than {@link #jDToCivilianAsPerNumericalRecipesInC} which
     * agrees with my implementation for a much narrower range (but still very amble for practical purposes)
     * and only works for integer Julian Dates.
     * <p>
     * See JUnit test cases code for more.
     *
     * @param jD the Julian Date (reckoned on the UT1 time zone)
     * @return a {@link YearMonthDayHourMinSecMil} instance that stands for the point in time (in the UT1 time zone)
     *         corresponding to that Julian Date
     * 
     */
    public static YearMonthDayHourMinSecMil jDToCivilianAsPerMeeusAstronomicalAlgorithms(double jD) {
        assert_non_negative_double(jD, "jD");
        jD += 0.5; // this is a fix I introduced to the algorithm that was necessary
        final int YEAR = 0;
        final int MONTH = 1;
        final int DAY = 2;
        final int HOURS = 3;
        final int MINUTES = 4;
        final int SECONDS = 5;
        final int MILLIS = 6;

        int ymd_hms[] = { -1, -1, -1, -1, -1, -1, -1 };

        int a, b, c, d, e, z;

        double f, x;

        z = (int) Math.floor(jD);
        f = jD - z;

        if (z >= 2299161) {
            int alpha = (int) Math.floor((z - 1867216.25) / 36524.25);
            a = z + 1 + alpha - (alpha / 4) ;
        } else {
            a = z;
        }

        b = a + 1524;
        c = (int) Math.floor((b - 122.1) / 365.25);
        d = (int) Math.floor(365.25 * c);
        e = (int) Math.floor((b - d) / 30.6001);

        ymd_hms[DAY] = b - d - (int) Math.floor(30.6001 * e);
        ymd_hms[MONTH] = (e < 14)
            ? (e - 1)
            : (e - 13);
        ymd_hms[YEAR] = (ymd_hms[MONTH] > 2)
            ? (c - 4716)
            : (c - 4715);

        for (int i = HOURS; i <= MILLIS; i++) {
            switch(i) {
            case HOURS:
                f *= 24.0;
                break;
            case MINUTES: case SECONDS:
                f *= 60.0;
                break;
            case MILLIS:
                f *= 1000.0;
                break;
            default:
                Assert.fail(String.format("unhandled case: [%d]"
                                          , i));
                break;
            }
            x = Math.floor(f);
            ymd_hms[i] = (int) x;
            f -= x;
        }

        /* The original algorithm by Jean Meeus uses year 0 for 1 BC; however my YearMonthDayHourMinSecMil expects -1
           As this affects all negative years, we shift them all by one.
        */
        if (ymd_hms[YEAR]<=0)
            ymd_hms[YEAR]--;

        return new YearMonthDayHourMinSecMil(ymd_hms[YEAR]
                                             , ymd_hms[MONTH]
                                             , ymd_hms[DAY]
                                             , ymd_hms[HOURS]
                                             , ymd_hms[MINUTES]
                                             , ymd_hms[SECONDS]
                                             , ymd_hms[MILLIS]);
    }

    /**
     * Converts a Julian Date value to a <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Date.html">java.util.Date</a>
     * <p>
     * The implementation is based on
     * {@link #jDToCivilianAsPerMeeusAstronomicalAlgorithms(double) jDToCivilianAsPerMeeusAstronomicalAlgorithms}
     * to generate a {@link YearMonthDayHourMinSecMil YearMonthDayHourMinSecMil} object which is, subsequently,
     * converted to a <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Date.html">java.util.Date</a> object
     * using {@link DateUtil#toTimeOnGivenTimeZone DateUtil#toTimeOnGivenTimeZone}.
     * 
     * @param jD  the Julian Date value
     * @return a {@link YearMonthDayHourMinSecMil} instance that provides the civilian time (in the UT1 time zone)
     *         for the instance in time that this Julian Date represents
     *
     */
    public static Date jDToDateAsPerJeanMeeus(double jD) {
        assert_non_negative_double(jD, "jD");
        final YearMonthDayHourMinSecMil x = jDToCivilianAsPerMeeusAstronomicalAlgorithms(jD);
        return DateUtil.toTimeOnGivenTimeZone(x, TZ_FOR_RECKONING_JULIAN_DATES);
    }

    public static double dateToJulianDate(final Date d) {
        final Date startOfTheJulianEra = startOfJulianEra();
        final long ms = d.getTime() - startOfTheJulianEra.getTime();
        return millisToDays(ms);
    }

    public static double dateToMJD(final Date d) {
        return dateToJulianDate(d) - 2_400_000.5;
    }    

    private static double millisToDays(final long ms) {
        final long msInADay = TimeUnit.DAYS.toMillis(1);
        return ((double) ms) / msInADay;
    }

}

