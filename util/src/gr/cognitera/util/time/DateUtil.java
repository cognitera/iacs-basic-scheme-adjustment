package gr.cognitera.util.time;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Locale;


/**
 * Utility functions for <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Date.html">java.util.Date</a> objects
 *
 *
 *
 */
public class DateUtil {

    private DateUtil() {}


    /**
     * Produces a {@link YearMonthDay} value given a <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Date.html">Date</a> and a <a href="https://docs.oracle.com/javase/7/docs/api/java/util/TimeZone.html">TimeZone</a>
     * 
     * @param date a <a href="https://docs.oracle.com/javase/7/docs/api/java/util/Date.html">Date</a> instance representing a
     *             specific instance in time
     * @param tz  a <a href="https://docs.oracle.com/javase/7/docs/api/java/util/TimeZone.html">TimeZone</a> instance representing
     *            the time zone for which we seek to derive the civilian time fields for year, month and day
     * @return a {@link YearMonthDay} instance representing civilian time on this specific instance in time for that particular time zone
     *
     */
    public static final YearMonthDay yearMonthDayOnTimeZone3(final Date date
                                                            , final TimeZone tz) {
        final Calendar calendarToUse = new GregorianCalendar(tz, Locale.ENGLISH);
        calendarToUse.setTime(date);
        final int multiplier = calendarToUse.get(Calendar.ERA)==GregorianCalendar.BC?-1:1;
        return new YearMonthDay(multiplier*calendarToUse.get(Calendar.YEAR)
                                , calendarToUse.get(Calendar.MONTH)+1
                                , calendarToUse.get(Calendar.DAY_OF_MONTH));
    }

    public static final YearMonthDayHourMinSecMil fieldsOnTimeZone(final Date date
                                                                   , final TimeZone tz) {
        final Calendar calendarToUse = new GregorianCalendar(tz, Locale.ENGLISH);
        calendarToUse.setTime(date);
        final int multiplier = calendarToUse.get(Calendar.ERA)==GregorianCalendar.BC?-1:1;
        return new YearMonthDayHourMinSecMil(multiplier*calendarToUse.get(Calendar.YEAR)
                                             , calendarToUse.get(Calendar.MONTH)+1
                                             , calendarToUse.get(Calendar.DAY_OF_MONTH)
                                             , calendarToUse.get(Calendar.HOUR_OF_DAY)
                                             , calendarToUse.get(Calendar.MINUTE)
                                             , calendarToUse.get(Calendar.SECOND)
                                             , calendarToUse.get(Calendar.MILLISECOND));
    }

    public static final Date toTimeOnGivenTimeZone(final YearMonthDayHourMinSecMil x
                                                   , final TimeZone tz) {
        final Calendar calendar = new GregorianCalendar(tz, Locale.ENGLISH);
        calendar.setLenient(false);
        calendar.clear();
        calendar.set(Calendar.ERA         , x.year>0?GregorianCalendar.AD:GregorianCalendar.BC);
        calendar.set(Calendar.YEAR        , x.year<0?-x.year:x.year);
        calendar.set(Calendar.MONTH       , x.month-1); // months in GregorianCalendar are 0-indexed
        calendar.set(Calendar.DAY_OF_MONTH, x.day); // for some weird design reason days in GregorianCalendar are 1-indexed (even though months are 0-indexed) so we can use the value as it is
        calendar.set(Calendar.HOUR_OF_DAY , x.hour);
        calendar.set(Calendar.MINUTE      , x.minute);
        calendar.set(Calendar.SECOND      , x.second);
        calendar.set(Calendar.MILLISECOND , x.millisecond);
        return calendar.getTime();
    }

    public static final String problemIfNotWithinMillis(final YearMonthDayHourMinSecMil a
                                                        , final YearMonthDayHourMinSecMil b
                                                        , final long msTolerance) {
        final TimeZone tz = TimeZone.getTimeZone("UT1");
        final long aMs = toTimeOnGivenTimeZone(a, tz).getTime();
        final long bMs = toTimeOnGivenTimeZone(b, tz).getTime();
        final long absDiff = Math.abs(aMs-bMs);
        if (absDiff > msTolerance)
            return String.format("Difference between [%s] and [%s] is [%d] ms which exceeds tolerance of [%d] ms"
                                 , a
                                 , b
                                 , absDiff
                                 , msTolerance);
        else
            return null;
    }

    public static final String problemIfNotWithinMillis(final Date a
                                                        , final Date b
                                                        , final long msTolerance) {
        final long aMs = a.getTime();
        final long bMs = b.getTime();
        final long absDiff = Math.abs(aMs-bMs);
        if (absDiff > msTolerance)
            return String.format("Difference between [%s] and [%s] is [%d] ms which exceeds tolerance of [%d] ms"
                                 , a
                                 , b
                                 , absDiff
                                 , msTolerance);
        else
            return null;
    }
}
