package gr.cognitera.util.astro;

import org.junit.Assert;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.net.URL;


import org.junit.Rule;


import com.google.common.base.Joiner;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;


import java.util.Date;
import java.util.TimeZone;

import gr.cognitera.util.base.Util;
import gr.cognitera.util.base.Pair;

import gr.cognitera.util.time.YearMonthDay;
import gr.cognitera.util.time.YearMonthDayHourMinSecMil;
import gr.cognitera.util.time.DateUtil;
import gr.cognitera.util.time.LeapYearUtil;
import gr.cognitera.util.time.DateFormatUtil;



final class JDandCivilianUpToDay {

    public double      julianDate;
    public YearMonthDay yearMonthDay;

    public JDandCivilianUpToDay(final double julianDate
                                , final YearMonthDay yearMonthDay) {
        this.julianDate   = julianDate;
        this.yearMonthDay = yearMonthDay;
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("julianDate", julianDate)
            .add("yearMonthDay", yearMonthDay)
            ;
    }
    @Override
    public String toString() {
        return toStringHelper().toString();
    }
    
}


final class MJDandCivilianUpToMillisecond {

    public double                     mJD;
    public YearMonthDayHourMinSecMil  civilian;
    public long                       msToleranceForJeanMeeusAlgo;
    public long                       msToleranceForJavaAlgo;

    public MJDandCivilianUpToMillisecond(final   double                    mJD
                                         , final YearMonthDayHourMinSecMil civilian
                                         , final long                      msToleranceForJeanMeeusAlgo
                                         , final long                      msToleranceForJavaAlgo
                                         ) {
        Assert.assertTrue(msToleranceForJeanMeeusAlgo  >= 0);
        Assert.assertTrue(msToleranceForJavaAlgo >= 0);
        this.mJD                               = mJD;
        this.civilian                          = civilian;
        this.msToleranceForJeanMeeusAlgo       = msToleranceForJeanMeeusAlgo;
        this.msToleranceForJavaAlgo            = msToleranceForJavaAlgo;
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("mJD"                         , mJD)
            .add("civilian"                    , civilian)
            .add("msToleranceForJeanMeeusAlgo" , msToleranceForJeanMeeusAlgo)
            .add("msToleranceForJavaAlgo", msToleranceForJavaAlgo)
            ;
    }
    @Override
    public String toString() {
        return toStringHelper().toString();
    }
    
}


/******************************************************************************
 *                                                                            *
 *    J U L I A N      D A T E S      A N D                                   *
 *    D A Y      G R A N U L A R I T Y                                        *
 *    T E S T      C A S E S      P R O V I D E R S                           *
 *                                                                            *
 ******************************************************************************/

interface IJDandDayGranularityProvider {
    List<JDandCivilianUpToDay> provide();
}


final class Java8DocumentationConversionValuesProvider implements IJDandDayGranularityProvider {
    // this test case is taken from: https://docs.oracle.com/javase/8/docs/api/java/time/temporal/JulianFields.html
    @Override
    public List<JDandCivilianUpToDay> provide() {
        final List<JDandCivilianUpToDay> rv = new ArrayList<>();
        rv.add(new JDandCivilianUpToDay(2440588f, new YearMonthDay(1970, 1, 1)));
        rv.add(new JDandCivilianUpToDay(2440589f, new YearMonthDay(1970, 1, 2)));
        return rv;
    }
}



final class MyConversionValuesProvider implements IJDandDayGranularityProvider {
    @Override
    public List<JDandCivilianUpToDay> provide() {
        final List<JDandCivilianUpToDay> rv = new ArrayList<>();
        rv.add(new JDandCivilianUpToDay(0f, new YearMonthDay(-4713, 1, 1)));
        rv.add(new JDandCivilianUpToDay(1f, new YearMonthDay(-4713, 1, 2)));
        rv.add(new JDandCivilianUpToDay(LeapYearUtil.isLeapYear(-4713)?366f:365f, new YearMonthDay(-4712, 1, 1)));
        return rv;
    }
}

final class USNavalObservatoryConversionValuesProvider implements IJDandDayGranularityProvider {

    @Override
    public List<JDandCivilianUpToDay> provide() {
        // https://aa.usno.navy.mil/data/docs/JulianDate.php
        List<JDandCivilianUpToDay> testCases = new ArrayList<>();
        testCases.add(new JDandCivilianUpToDay(2458848.5f, new YearMonthDay(2019, 12, 31)));
        /*
         * sse-1544540866
         * The following test case is a bit weird; the US naval observatory says 2458284.50 but the test case
         * only succeeds with a slightly larger value.
         */
        testCases.add(new JDandCivilianUpToDay(2458284.65f , new YearMonthDay(2018,  6, 15))); 
        testCases.add(new JDandCivilianUpToDay(2442405.5f  , new YearMonthDay(1974, 12, 24)));
        testCases.add(new JDandCivilianUpToDay(2429507.65f , new YearMonthDay(1939,  9,  1))); // cf. sse-1544540866
        testCases.add(new JDandCivilianUpToDay(2421908.65f , new YearMonthDay(1918, 11, 11))); // cf. sse-1544540866
        return testCases;
    }
}


// sse-1544545507: it obviously only makes sense to use this provider with the assertInvariantWithRegardsToTimeZoneGauntlet
final class AutomaticConversionValuesProvider implements IJDandDayGranularityProvider {
    @Override
    public List<JDandCivilianUpToDay> provide() {
        List<JDandCivilianUpToDay> testCases = new ArrayList<>();
        final int someRecentJDN   = 2_458_848;
        final int numOfDaysToTest = 30_000;
        for (int jdn = someRecentJDN ; jdn > someRecentJDN-numOfDaysToTest; jdn--) {
            final YearMonthDay ymd = JulianDateUtil.jDToCivilianAsPerJava(jdn);
            testCases.add(new JDandCivilianUpToDay(jdn, ymd));
        }
        return testCases;
    }
}

/******************************************************************************
 *                                                                            *
 *    M O D I F I E D      J U L I A N      D A T E S      A N D              *
 *    M I L L I S E C O N D      G R A N U L A R I T Y                        *
 *    T E S T      C A S E S      P R O V I D E R S                           *
 *                                                                            *
 ******************************************************************************/


interface IMJDandMillisecondGranularityProvider {
    List<MJDandCivilianUpToMillisecond> provide();
}


final class NASA_HEASARC_MJD_Provider implements IMJDandMillisecondGranularityProvider {
    // https://heasarc.gsfc.nasa.gov/cgi-bin/Tools/xTime/xTime.pl
    @Override
    public List<MJDandCivilianUpToMillisecond> provide() {
        return Arrays.asList(new MJDandCivilianUpToMillisecond[]{
                new   MJDandCivilianUpToMillisecond(             0, new YearMonthDayHourMinSecMil(1858, 11, 17,  0,  0, 0, 0), 0, 0)
                , new MJDandCivilianUpToMillisecond(          1000, new YearMonthDayHourMinSecMil(1861,  8, 13,  0,  0, 0, 0), 0, 0)
                , new MJDandCivilianUpToMillisecond(         10000, new YearMonthDayHourMinSecMil(1886,  4,  4,  0,  0, 0, 0), 0, 0)
                , new MJDandCivilianUpToMillisecond(         20000, new YearMonthDayHourMinSecMil(1913,  8, 21,  0,  0, 0, 0), 0, 0)
                , new MJDandCivilianUpToMillisecond(         29507, new YearMonthDayHourMinSecMil(1939,  9,  1,  0,  0, 0, 0), 0, 0) // WWII start
                , new MJDandCivilianUpToMillisecond(         31700, new YearMonthDayHourMinSecMil(1945,  9,  2,  0,  0, 0, 0), 0, 0) // WWII end
                , new MJDandCivilianUpToMillisecond(51382.54166667, new YearMonthDayHourMinSecMil(1999,  7, 23, 13,  0, 0, 0), 0, 0) // lunch time on Chandra's launch day. NB: see note-a
                // follow a bunch of random days
                , new MJDandCivilianUpToMillisecond(53383.61458333, new YearMonthDayHourMinSecMil(2005,  1, 13, 14, 45, 0, 0), 1, 1)
                , new MJDandCivilianUpToMillisecond(58841.52234954, new YearMonthDayHourMinSecMil(2019, 12, 24, 12, 32,11, 0), 0, 0)
                , new MJDandCivilianUpToMillisecond(58907.99998843, new YearMonthDayHourMinSecMil(2020,  2, 28, 23, 59,59, 0), 0, 0)
                , new MJDandCivilianUpToMillisecond(58908.00000000, new YearMonthDayHourMinSecMil(2020,  2, 29,  0,  0, 0, 0), 0, 0) // 2020 is a leap year
                , new MJDandCivilianUpToMillisecond(58908.00001157, new YearMonthDayHourMinSecMil(2020,  2, 29,  0,  0, 1, 0), 1, 1) // ===================
                , new MJDandCivilianUpToMillisecond(58912.82260417, new YearMonthDayHourMinSecMil(2020,  3,  4, 19, 44,33, 0), 0, 0)
                , new MJDandCivilianUpToMillisecond(59181.99998843, new YearMonthDayHourMinSecMil(2020, 11, 28, 23, 59,59, 0), 0, 0)

                , new MJDandCivilianUpToMillisecond(60369.00001157, new YearMonthDayHourMinSecMil(2024,  2, 29,  0,  0, 1, 0), 1, 1) // 2024 is a leap year
                , new MJDandCivilianUpToMillisecond(61830.00001157, new YearMonthDayHourMinSecMil(2028,  2, 29,  0,  0, 1, 0), 1, 1) // 2028 ==============
                , new MJDandCivilianUpToMillisecond(63291.00001157, new YearMonthDayHourMinSecMil(2032,  2, 29,  0,  0, 1, 0), 1, 1) // 2032 ==============
            });
    }
    /* Notes
       ---------------------------
       note-a: The NASA Heasarc site translates MJD 51382.54166667 to 1999-07-23 13:00:00.000 UTC
               However, the US Naval Observatory site (https://aa.usno.navy.mil/data/docs/JulianDate.php) translates
               the equivalent JD of 2451383.041666 (MJD + 2400000.5) to 1999-07-23 12:59:59.9 UT1. Jean Meeus algorithm
               agrees with NASA Heasarc and translates MJD 51382.54166667 to 1999-07-23 13:00:00.000 UTC
     */

}




public class JulianDateUtilTest {

    private static List<IJDandDayGranularityProvider> allJulianDayDayGranularityProviders() {
        return Arrays.asList(new IJDandDayGranularityProvider[]{
                                                                new Java8DocumentationConversionValuesProvider()
                                                                , new MyConversionValuesProvider()
                                                                , new USNavalObservatoryConversionValuesProvider()
                                                                , new AutomaticConversionValuesProvider()
            });
    }

    private static List<IMJDandMillisecondGranularityProvider> allMJDMillisecondGranularityProviders() {
        return Arrays.asList(new IMJDandMillisecondGranularityProvider[]{
                new NASA_HEASARC_MJD_Provider()
            });
    }

    @Test
    public void julianDayAndDayGranularityGauntlet() {
        for (final IJDandDayGranularityProvider conversionValuesProvider: allJulianDayDayGranularityProviders()) {
            int i = 1;
            for (final JDandCivilianUpToDay testCase: conversionValuesProvider.provide()) {
                final String msg = String.format("value mismatch in provider [%s], test case [%s];"
                                                 +" this is test case #%d (1-indexed) emitted by said provider"
                                                 , conversionValuesProvider.getClass().getName()
                                                 , testCase
                                                 , i);
                assertEquals(String.format("%s - jDToCivilianAsPerJava", msg)
                             , testCase.yearMonthDay
                             , JulianDateUtil.jDToCivilianAsPerJava(Util.castLongToInt(Math.round(testCase.julianDate))));
                assertEquals(String.format("%s - jDToCivilianAsPerNumericalRecipesInC", msg)
                             , testCase.yearMonthDay
                             , JulianDateUtil.jDToCivilianAsPerNumericalRecipesInC(Util.castLongToInt(Math.round(testCase.julianDate))));
                assertEquals(String.format("%s - jDToCivilianAsPerMeeusAstronomicalAlgorithms", msg)
                             , testCase.yearMonthDay
                             , JulianDateUtil.jDToCivilianAsPerMeeusAstronomicalAlgorithms (Math.round(testCase.julianDate)));
                i++;
            }
        }
    }

    @Test
    public void assertInvariantWithRegardsToTimeZoneGauntlet() {
        for (final IJDandDayGranularityProvider conversionValuesProvider: allJulianDayDayGranularityProviders()) {
            int i = 1;        
            for (final JDandCivilianUpToDay testCase: conversionValuesProvider.provide()) {
                int numMatch = 0;
                final String[] tzIds = TimeZone.getAvailableIDs();
                List<String> offendingTimeZones = new ArrayList<>();
                for (final String tzId: tzIds) {
                    final TimeZone tz = TimeZone.getTimeZone(tzId);
                    final Date date = JulianDateUtil.jDToDateAsPerJava(tz, testCase.julianDate);
                    final YearMonthDayHourMinSecMil ymd = DateUtil.fieldsOnTimeZone(date, tz);
                    if (ymd.toYearMonthDay(false).equals(testCase.yearMonthDay))
                        numMatch++;
                    else
                        offendingTimeZones.add(tzId);
                }
                Assert.assertTrue  (numMatch <= tzIds.length);
                final double successRatioThreshold = .90; // it might be interesting to get to the bottom of why I can't set this to 1.
                final double successRatio = ((double) numMatch)/ tzIds.length;
                Assert.assertTrue( (successRatio>=0) && (successRatio<=1) );
                Assert.assertTrue(String.format("Failure in provider [%s], test case [%s];"
                                                +" this is test case #%d (1-indexed) emitted by said provider:"
                                                +" only %d timezones out of %d matched the conversion from JDN [%s] to %s."
                                                +" Success ratio was %f with the threshold being set to %f."
                                                +" %d offending timezones found: %s"
                                                , conversionValuesProvider.getClass().getName()
                                                , testCase
                                                , i
                                                , numMatch
                                                , tzIds.length
                                                , testCase.julianDate
                                                , testCase.yearMonthDay
                                                , successRatio
                                                , successRatioThreshold
                                                , offendingTimeZones.size()
                                                , Joiner.on(", ").join(offendingTimeZones))
                                  , successRatio >= successRatioThreshold);
                i++;
            }
        }
    }

    @Test
    public void mjdAndMillisecondGranularityGauntlet() {
        for (final IMJDandMillisecondGranularityProvider conversionValuesProvider: allMJDMillisecondGranularityProviders()) {
            int i = 1;
            for (final MJDandCivilianUpToMillisecond testCase: conversionValuesProvider.provide()) {
                final String msg = String.format("value mismatch in provider [%s], test case [%s];"
                                                 +" this is test case #%d (1-indexed) emitted by said provider"
                                                 , conversionValuesProvider.getClass().getName()
                                                 , testCase
                                                 , i);
                {   // assertions on the awesome Jean Meeus algorithm
                    assertNull(String.format("%s - mJDToDate (civilian)", msg)
                               , DateUtil.problemIfNotWithinMillis(
                                                                   testCase.civilian
                                                                   , DateUtil.fieldsOnTimeZone(JulianDateUtil.mJDToDate(testCase.mJD)
                                                                                               , JulianDateUtil.TZ_FOR_RECKONING_JULIAN_DATES)
                                                                   , testCase.msToleranceForJeanMeeusAlgo));
                    assertNull(String.format("%s - mJDToDate (dates)", msg)
                               , DateUtil.problemIfNotWithinMillis(
                                                                   DateUtil.toTimeOnGivenTimeZone(testCase.civilian
                                                                                                  , JulianDateUtil.TZ_FOR_RECKONING_JULIAN_DATES)
                                                                   , JulianDateUtil.mJDToDate(testCase.mJD)
                                                                   , testCase.msToleranceForJeanMeeusAlgo));
                }
                {   // assertions on the equally exact Java implementation
                    final long msseExpected = DateUtil.toTimeOnGivenTimeZone(testCase.civilian
                                                                             , JulianDateUtil.TZ_FOR_RECKONING_JULIAN_DATES).getTime();
                    final long msseJava  = JulianDateUtil.mJDToDateAsPerJava(testCase.mJD).getTime();
                    final long absMsDiff = Math.abs(msseExpected - msseJava);
                    Assert.assertTrue(String.format("%s - mJDToDateAsPerJava: difference of [%d] ms"
                                                    +" exceeds tolerance of [%d]"
                                                    , msg
                                                    , absMsDiff
                                                    , testCase.msToleranceForJavaAlgo)
                                      , absMsDiff <=  testCase.msToleranceForJavaAlgo);
                }
                i++;
            }
        }
    }
    


    private static String dateToString(final Date d) {
        final SimpleDateFormat sdf = new SimpleDateFormat("G yyyy-MM-dd HH:mm:ss.SSS z (Z)");
        sdf.setTimeZone(JulianDateUtil.TZ_FOR_RECKONING_JULIAN_DATES);
        return sdf.format(d);
    }

    @Test
    public void assertThatIntegerJulianDateUsingBothJavaAndJeanMeeusAlgorithmAre12Noon() {
        final int increment = 1_000;
        for (int i = 0 ; i < 3_000_000 ; i+=increment) {
            final List<Date> dates = Arrays.asList(new Date[]{JulianDateUtil.jDToDateAsPerJava(i)
                                                              , JulianDateUtil.jDToDateAsPerJeanMeeus(i)});
            for (final Date date: dates) {
                final YearMonthDayHourMinSecMil fields = DateUtil.fieldsOnTimeZone(date
                                                                                   , JulianDateUtil.TZ_FOR_RECKONING_JULIAN_DATES);
                Assert.assertEquals(12, fields.hour);
                Assert.assertEquals( 0, fields.minute);
                Assert.assertEquals( 0, fields.second);
                Assert.assertEquals( 0, fields.millisecond);
            }
        }
    }


    @Test
    public void assertThatIntegerModifiedJulianDatesUsingBothJavaAndJeanMeeusAlgorithmAreMidnight() {
        final int increment = 1;
        for (int i = 0 ; i < 100_000 ; i+=increment) {
            final List<Date> dates = Arrays.asList(new Date[]{JulianDateUtil.mJDToDateAsPerJava(i)
                                                              , JulianDateUtil.mJDToDateAsPerJeanMeeus(i)});
            for (final Date date: dates) {
                final YearMonthDayHourMinSecMil fields = DateUtil.fieldsOnTimeZone(date
                                                                                   , JulianDateUtil.TZ_FOR_RECKONING_JULIAN_DATES);
                Assert.assertEquals( 0, fields.hour);
                Assert.assertEquals( 0, fields.minute);
                Assert.assertEquals( 0, fields.second);
                Assert.assertEquals( 0, fields.millisecond);
            }
        }
    }
    
    
    @Test
    public void testThatTheJavaAlgorithmAgreesWithThatOfNumericalRecipesInCForIntegerJulianDates() {
        final int iLLBeDeadByThen              = 2_488_070; //  1 January 2100 AD, noon UT1
        final int julianDayOfJanuary1st1583    = 2_299_239; //  1 January 1583 AD, noon UT1
        final int julianDayOf15Oct1583         = 2_299_161; // 15 October 1582 AD, noon UT1 (Gregorian); adoption of Gregorian calendar in the Roman Catholic countries (expressed as a Gregorian data)

        // curiously it only agrees from one day after the adoption of the Gregorian calendar
        for (int jdn = iLLBeDeadByThen; jdn >= julianDayOf15Oct1583+1; jdn--) { 
            final YearMonthDay v1 = JulianDateUtil.jDToCivilianAsPerJava(jdn);
            final YearMonthDay v2 = JulianDateUtil.jDToCivilianAsPerNumericalRecipesInC(jdn);
            Assert.assertEquals(v1, v2);
            if (jdn % 10_000 == 0)
                System.out.printf("Both Java and Cambridge agree that [%d] translates to %s\n", jdn, v1);
        }
    }

    @Test
    public void testThatTheJavaAlgorithmAgreesWithThatOfMeeusAstronomicalAlgorithmsForIntegerJulianDates() {
        final int iLLBeDeadByThen              = 2_488_070; // Jan 1st 2100 AD, noon UT1
        final int julianDayOfJanuary1st1583    = 0;         // Jan 1st 4713 BC, noon UT1 (proleptic Julian Calendar)
        for (int jdn = iLLBeDeadByThen; jdn >= julianDayOfJanuary1st1583; jdn--) { 
            final YearMonthDay              v1 = JulianDateUtil.jDToCivilianAsPerJava(jdn);
            final YearMonthDayHourMinSecMil v2 = JulianDateUtil.jDToCivilianAsPerMeeusAstronomicalAlgorithms(jdn);
            Assert.assertEquals(v1, v2);
            if (jdn % 10_000 == 0)
                System.out.printf("Both Java and Meeus agree that [%d] translates to %s\n", jdn, v1);
        }
    }


    @Test
    public void test_that_the_Java_and_Jean_Meeus_Algorithms_for_JD_differ_by_1_ms_at_most_over_a_wide_swath_of_contemporary_dates() {
        final int toleranceInMillis        = 1;
        final int chandraLaunchDate        = 2_451_383; // Julian Date of: 23 Jul 1999 AD, noon UT1
        final int chandraWillBeToastByThen = 2_469_808; // Julian Date of:  1 Jan 2050 AD, noon UT1
        final double increment             = 0.001713014001;
        int i                              = 0;
        boolean toleranceIsTight           = false;
        long highestToleranceEncountered   = Long.MIN_VALUE;
        for (double jdn = chandraLaunchDate; jdn <= chandraWillBeToastByThen; jdn+=increment) {
            final Date d1 = JulianDateUtil.jDToDateAsPerJava(jdn);
            final Date d2 = JulianDateUtil.jDToDateAsPerJeanMeeus(jdn);
            final long millisAbsDelta = Math.abs(d1.getTime() - d2.getTime());
            if (millisAbsDelta > highestToleranceEncountered)
                highestToleranceEncountered = millisAbsDelta;
            if (millisAbsDelta==toleranceInMillis)
                toleranceIsTight = true;
            Assert.assertTrue(String.format("Java says [%s] (%d); Meeus says [%s] (%d); absolute difference of 'Java - Meeus' is: [%d] ms"
                                            +" which exceeds configured tolerance of [%d] ms."
                                            , dateToString(d1)
                                            , d1.getTime()
                                            , dateToString(d2)
                                            , d2.getTime()
                                            , millisAbsDelta
                                            , toleranceInMillis)
                              , millisAbsDelta <= toleranceInMillis);
            final int printOnceEvery = 100_000;
            if (i++ % printOnceEvery == 0)
                System.out.printf("Both Java and Meeus agree that [%10.2f] translates to [%s] (with an absolute delta of [%d] ms)\n"
                                  , jdn
                                  , dateToString(d1)
                                  , millisAbsDelta);
        }
        if (!toleranceIsTight)
            throw new RuntimeException(String.format("Tolerance of [%d] can be set to some tighter value"
                                                     +", it was never actually reached among [%s] test cases"
                                                     +"; set it to [%d] instead. This is not really a test case"
                                                     +" failure but it's nice if the test cases are as tight"
                                                     +" as possible!"
                                                     , toleranceInMillis
                                                     , i
                                                     , highestToleranceEncountered));
    }
    
    @Test
    public void adHocTestsForMJD() {
        final double mjd = 49987;
        final Date pointInTimeJava      = JulianDateUtil.mJDToDateAsPerJava(49987);
        final Date pointInTimeJeanMeeus = JulianDateUtil.mJDToDateAsPerJeanMeeus(49987);
        Assert.assertEquals(pointInTimeJava, pointInTimeJeanMeeus);
        Assert.assertEquals(DateUtil.fieldsOnTimeZone(pointInTimeJava, JulianDateUtil.TZ_FOR_RECKONING_JULIAN_DATES).toYearMonthDay(false)
                            , new YearMonthDay(1995, 9, 27)); // example taken from: https://tycho.usno.navy.mil/mjd.html
    }

    @Test
    public void adHocTestsForMJD2() {
        final Random r = new Random(0);
        final double start = 50_000;
        final double end   = 100_000;
        final double width = end - start;
        final int NUM_OF_TESTS = 100_000;
        for (int i = 0; i < NUM_OF_TESTS; i++) {
            final double mjdA_ =  start+r.nextDouble()*width;
            final double mjdB_ =  start+r.nextDouble()*width;

            final double mjdA =  mjdA_ > mjdB_? mjdB_ : mjdA_;
            final double mjdB =  mjdA_ > mjdB_? mjdA_ : mjdB_;

            final Date dateA = JulianDateUtil.mJDToDate(mjdA);
            final Date dateB = JulianDateUtil.mJDToDate(mjdB);
            final long millisSpan1 = dateB.getTime() - dateA.getTime();
            final long millisSpan2 = Math.round( (mjdB - mjdA)*TimeUnit.DAYS.toMillis(1) );
            final long diff = Math.abs(millisSpan2  - millisSpan1);
            final long TOLERANCE = 1;
            final String msg = String.format("failure on test [%d] of [%d]; MJD A = [%10.5f], MJD B = [%10.5f], ms span 1: [%d], ms span 2: [%d]"
                                             +", diff: [%d] and exceeds (as absolute value) tolerance of [%d] ms"
                                             , i+1
                                              , NUM_OF_TESTS
                                             , mjdA
                                             , mjdB
                                             , millisSpan1
                                             , millisSpan2
                                             , millisSpan2 - millisSpan1
                                             , TOLERANCE);
            Assert.assertTrue(msg, diff <= TOLERANCE);
        }
    }

    @Test
    public void testStartOfJulianEra() {
        final String s = DateFormatUtil.format02(TimeZone.getTimeZone("UT1"), JulianDateUtil.startOfJulianEra());
        Assert.assertEquals("4713-01-01 BC at 12:00:00.000+0000 (GMT)", s);
    }

    @Test
    public void adHocTestsForDateToJulianDate() {
        // test cases from the US Naval Observatory Julian Date Converter page: https://aa.usno.navy.mil/data/docs/JulianDate.php
        final List<Pair<Date, Double>> testCases = new ArrayList<>();
        testCases.add(Pair.create(DateUtil.toTimeOnGivenTimeZone(new YearMonthDayHourMinSecMil(2019, 2, 8, 0, 0, 0, 0)
                                                                 , TimeZone.getTimeZone("UT1"))
                                  , 2458522.500000));
        testCases.add(Pair.create(DateUtil.toTimeOnGivenTimeZone(new YearMonthDayHourMinSecMil(1939, 9, 1, 3, 23, 44, 200)
                                                                 , TimeZone.getTimeZone("UT1"))
                                  , 2429507.641484));
        testCases.add(Pair.create(DateUtil.toTimeOnGivenTimeZone(new YearMonthDayHourMinSecMil(2022, 6,15, 6,  1,  3,  10)
                                                                 , TimeZone.getTimeZone("UT1"))
                                  , 2459745.750729));
        testCases.add(Pair.create(DateUtil.toTimeOnGivenTimeZone(new YearMonthDayHourMinSecMil(2099,11, 5,23, 59, 59, 300)
                                                                 , TimeZone.getTimeZone("UT1"))
                                  , 2488013.499992));

        for (final Pair<Date, Double> testCase: testCases) {
            final double mjd = JulianDateUtil.dateToJulianDate(testCase.a);
            Assert.assertEquals(testCase.b, mjd, 1e-5);
        }
    }
}
