package gr.cognitera.util.time;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.everyItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.net.URL;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import org.junit.rules.ErrorCollector;
import org.junit.Rule;

import javax.xml.bind.DatatypeConverter;

import com.google.common.base.Joiner;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
    
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import gr.cognitera.util.base.Pair;


public class GregorianCalendarExploratoryTest {

    
    @Test
    public void test_that_Year_0_BC_is_the_same_as_Year_1_AD_in_lenient_mode() {
        final GregorianCalendar calendarA = new GregorianCalendar(TimeZone.getTimeZone("UTC")
                                                        , Locale.US);
        Assert.assertTrue(calendarA.isLenient());
        final GregorianCalendar calendarB = (GregorianCalendar) calendarA.clone();
        calendarA.set(Calendar.ERA, GregorianCalendar.BC);
        calendarA.set(Calendar.YEAR, 0);
        calendarB.set(Calendar.ERA, GregorianCalendar.AD);
        calendarB.set(Calendar.YEAR, 1);
        Assert.assertEquals(calendarA.getTimeInMillis(), calendarB.getTimeInMillis());
    }

    @Test
    public void test_that_Year_0_AD_is_the_same_as_Year_1_BC_in_lenient_mode() {
        final GregorianCalendar calendarA = new GregorianCalendar(TimeZone.getTimeZone("UTC")
                                                        , Locale.US);
        Assert.assertTrue(calendarA.isLenient());
        final GregorianCalendar calendarB = (GregorianCalendar) calendarA.clone();
        calendarA.set(Calendar.ERA, GregorianCalendar.AD);
        calendarA.set(Calendar.YEAR, 0);
        calendarB.set(Calendar.ERA, GregorianCalendar.BC);
        calendarB.set(Calendar.YEAR, 1);
        Assert.assertEquals(calendarA.getTimeInMillis(), calendarB.getTimeInMillis());
    }


    @Test
    public void test_for_BC_AD_symmetries_in_lenient_mode() {
        final GregorianCalendar calendarA = new GregorianCalendar(TimeZone.getTimeZone("UTC")
                                                        , Locale.US);
        Assert.assertTrue(calendarA.isLenient());
        final GregorianCalendar calendarB = (GregorianCalendar) calendarA.clone();
        for (int yearAD = 10; yearAD > -10; yearAD--) {
            calendarA.set(Calendar.ERA, GregorianCalendar.AD);
            calendarA.set(Calendar.YEAR, yearAD);
            calendarB.set(Calendar.ERA, GregorianCalendar.BC);
            final int yearBC = -(yearAD-1);
        calendarB.set(Calendar.YEAR, yearBC);
            Assert.assertEquals(calendarA.getTimeInMillis(), calendarB.getTimeInMillis());
            System.out.printf("AD [%d] is the same as BC [%d] in Lenient mode\n"
                              , yearAD
                              , yearBC);
        }
    }
    

    @Test
    public void testNonLenientMode() {
        final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC")
                                                                 , Locale.US);
        Assert.assertTrue(calendar.isLenient());
        calendar.setLenient(false);
        List<Pair<Integer, Integer>> illegalValues = Arrays.asList(new Pair<>(GregorianCalendar.AD, 0)
                                                                   , new Pair<>(GregorianCalendar.AD, -1)
                                                                   , new Pair<>(GregorianCalendar.AD, -2)
                                                                   , new Pair<>(GregorianCalendar.BC, 0)
                                                                   , new Pair<>(GregorianCalendar.BC, -1)
                                                                   , new Pair<>(GregorianCalendar.BC, -2));
                                                                   
        for (final Pair<Integer, Integer> illegalValue: illegalValues) {
            calendar.set(Calendar.ERA , illegalValue.a);
            calendar.set(Calendar.YEAR, illegalValue.b);
            try {
                calendar.getTimeInMillis(); /* Used only for the side effect as the exception is only thrown
                                               when triggered by some computation.
                                            */
            } catch (Throwable t) {
                Assert.assertEquals(t.getClass(), IllegalArgumentException.class);
                final IllegalArgumentException e = (IllegalArgumentException) t;
                Assert.assertEquals(e.getMessage(), "YEAR");
                continue;
            }
            Assert.fail(String.format("was expecting an exception to be thrown with ERA=[%s], YEAR=[%s]"
                                      , calendar.getDisplayName(illegalValue.a, Calendar.LONG, Locale.US)
                                      , illegalValue.b));
        }
    }

    @Test
    public void test_default_smaller_unit_fields() {
        final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC")
                                                                 , Locale.US);
        calendar.setLenient(false);
        calendar.clear();
        calendar.set(Calendar.ERA         , GregorianCalendar.AD);
        calendar.set(Calendar.YEAR        , 1974);
        calendar.set(Calendar.MONTH       , Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 24);
        final Date date = calendar.getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss a z (Z)");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.printf("date is: %s\n", sdf.format(date));
        final long expectedMillis = 157075200000L; // value obtained from https://www.epochconverter.com/ for 00:00:00AM on 24 Dec, 1974 (Zulu time)
        Assert.assertEquals(expectedMillis, date.getTime());
    }

    @Test
    public void test_am_pm() {
        Date date1 = null;
        final long expectedMillisFor24DEC1974 = 157075200000L; // value obtained from https://www.epochconverter.com/ for 00:00:00AM on 24 Dec, 1974 (Zulu time)
        {
            final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC")
                                                                     , Locale.US);
            calendar.setLenient(false);
            calendar.clear();
            calendar.set(Calendar.ERA         , GregorianCalendar.AD);
            calendar.set(Calendar.YEAR        , 1974);
            calendar.set(Calendar.MONTH       , Calendar.DECEMBER);
            calendar.set(Calendar.DAY_OF_MONTH, 24);
            calendar.set(Calendar.HOUR, 0); // Calendar.HOUR is used for the 12 hour clock (0-11)
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.AM_PM, Calendar.AM);
            date1 = calendar.getTime();
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss a z (Z)");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            System.out.printf("date is: %s\n", sdf.format(date1));
            Assert.assertEquals(expectedMillisFor24DEC1974, date1.getTime());
        }
        Date date2 = null;
        {
            final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC")
                                                                     , Locale.US);
            calendar.setLenient(true); // we have to set this to true as we are using value 12 on the Calendar.HOUR field
            calendar.clear();
            calendar.set(Calendar.ERA         , GregorianCalendar.AD);
            calendar.set(Calendar.YEAR        , 1974);
            calendar.set(Calendar.MONTH       , Calendar.DECEMBER);
            calendar.set(Calendar.DAY_OF_MONTH, 24);
            calendar.set(Calendar.HOUR, 12); // Calendar.HOUR is used for the 12 hour clock (0-11)
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.AM_PM, Calendar.PM);
            date2 = calendar.getTime();
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss a z (Z)");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            System.out.printf("date is: %s\n", sdf.format(date2));
            Assert.assertEquals(expectedMillisFor24DEC1974+TimeUnit.DAYS.toMillis(1), date2.getTime());
        }
        {
            final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC")
                                                                     , Locale.US);
            calendar.setLenient(false);
            calendar.clear();
            calendar.set(Calendar.ERA         , GregorianCalendar.AD);
            calendar.set(Calendar.YEAR        , 1974);
            calendar.set(Calendar.MONTH       , Calendar.DECEMBER);
            calendar.set(Calendar.DAY_OF_MONTH, 25);
            calendar.set(Calendar.HOUR, 0); // Calendar.HOUR is used for the 12 hour clock (0-11)
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.AM_PM, Calendar.AM);
            final Date date3 = calendar.getTime();
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss a z (Z)");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            System.out.printf("date is: %s\n", sdf.format(date3));
            Assert.assertEquals(date2.getTime(), date3.getTime());
        }
    }

    @Test
    public void test_00_vs_24_hour() {
        Date date1 = null;
        final long expectedMillisFor24DEC1974 = 157075200000L; // value obtained from https://www.epochconverter.com/ for 00:00:00AM on 24 Dec, 1974 (Zulu time)
        {
            final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC")
                                                                     , Locale.US);
            calendar.setLenient(false);
            calendar.clear();
            calendar.set(Calendar.ERA         , GregorianCalendar.AD);
            calendar.set(Calendar.YEAR        , 1974);
            calendar.set(Calendar.MONTH       , Calendar.DECEMBER);
            calendar.set(Calendar.DAY_OF_MONTH, 24);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.AM_PM, Calendar.AM);
            date1 = calendar.getTime();
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss a z (Z)");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            System.out.printf("date is: %s\n", sdf.format(date1));
            Assert.assertEquals(expectedMillisFor24DEC1974, date1.getTime());
        }
        Date date2 = null;
        {
            final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC")
                                                                     , Locale.US);
            calendar.setLenient(true); // we have to set this to lenient because we're using an illegal hour of day (24); in the 24 hour clock valid values are in the [0, 23] range
            calendar.clear();
            calendar.set(Calendar.ERA         , GregorianCalendar.AD);
            calendar.set(Calendar.YEAR        , 1974);
            calendar.set(Calendar.MONTH       , Calendar.DECEMBER);
            calendar.set(Calendar.DAY_OF_MONTH, 24);
            calendar.set(Calendar.HOUR_OF_DAY, 24);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.AM_PM, Calendar.PM);
            date2 = calendar.getTime();
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss a z (Z)");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            System.out.printf("date is: %s\n", sdf.format(date2));
            Assert.assertEquals(expectedMillisFor24DEC1974+TimeUnit.DAYS.toMillis(1), date2.getTime());
        }
        {
            final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC")
                                                                     , Locale.US);
            calendar.setLenient(false);
            calendar.clear();
            calendar.set(Calendar.ERA         , GregorianCalendar.AD);
            calendar.set(Calendar.YEAR        , 1974);
            calendar.set(Calendar.MONTH       , Calendar.DECEMBER);
            calendar.set(Calendar.DAY_OF_MONTH, 25);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.AM_PM, Calendar.AM);
            final Date date3 = calendar.getTime();
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss a z (Z)");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            System.out.printf("date is: %s\n", sdf.format(date3));
            Assert.assertEquals(date2.getTime(), date3.getTime());
        }
    }
}
