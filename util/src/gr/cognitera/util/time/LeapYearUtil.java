package gr.cognitera.util.time;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class LeapYearUtil {


    private LeapYearUtil() {}

    /**
     * reports if a given year is a Leap Year or not
     * <p>
     * The method works for BC years as well &mdash; with the caveat
     * that there is no year zero, so if you pass 0, it gets treated the
     * same as -1 which is the year that came right before 1 AD)
     *
     * @param year  the year in question; there's no year 0, so if you pass 0 it gets treated 
     *              as -1 (i.e. the year preceding 1 AD)
     *
     */
    public static boolean isLeapYear(int year) {
        if (year==0)
            year = -1;
        final Calendar cal = Calendar.getInstance();
        cal.setLenient(false);
        cal.clear();
        if (year<0) {
            cal.set(Calendar.ERA, GregorianCalendar.BC);
            cal.set(Calendar.YEAR, -year);
        } else
            cal.set(Calendar.YEAR, year);
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
    }
}
