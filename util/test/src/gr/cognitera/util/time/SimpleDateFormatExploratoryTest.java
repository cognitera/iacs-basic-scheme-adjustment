package gr.cognitera.util.time;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;


import java.util.Date;
import java.util.TimeZone;
import java.util.List;
import java.util.Arrays;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.util.concurrent.TimeUnit;
    
public class SimpleDateFormatExploratoryTest {


    @Test
    public void mask_X_used_for_formatting_assumes_default_timezone_unless_TZ_is_explicitly_set_on_SDF_object() {
        final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SX";
        final SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        final Date now = new Date();

        String nowS = sdf.format(now);

        final int offsetFromUTCForDefaultTZ = TimeZone.getDefault().getRawOffset();
        if (offsetFromUTCForDefaultTZ!=0)
            assertFalse(nowS.endsWith("Z"));

        sdf.setTimeZone(TimeZone.getTimeZone("Zulu"));
        nowS = sdf.format(now);
        assertTrue(nowS.endsWith("Z"));
    }

    @Test
    /*
      This test proves that the presence of a hardcoded Z in the pattern is not translated to any particular time zone
      when parsing; instead, a Date object is created by assuming that the stringified representation of the date provided
      is to be interpreted in the default time zone of the machine.
     */
    public void hardcoded_Z_in_SDF_a() throws Exception{
        final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.S";
        final String PATTERN_WT_HARDCODED_Z = String.format("%s'Z'", PATTERN);
        final SimpleDateFormat sdfHardZ               = new SimpleDateFormat(PATTERN_WT_HARDCODED_Z);
        final SimpleDateFormat sdfNoSuffix            = new SimpleDateFormat(PATTERN);
        final SimpleDateFormat sdfNoSuffixOnDefaultTZ = new SimpleDateFormat(PATTERN);
        sdfNoSuffixOnDefaultTZ.setTimeZone(TimeZone.getDefault());
        final String dateS = "2019-01-07T15:02:52.371";
        final String dateS2 = String.format("%sZ", dateS);
        final Date d  = sdfNoSuffix.parse(dateS);        
        final Date d2 = sdfHardZ.parse(dateS2);
        final Date d3 = sdfNoSuffixOnDefaultTZ.parse(dateS);
        assertEquals(d, d2);
        assertEquals(d, d3);
    }

    @Test
    public void hardcoded_Z_in_SDF_b() throws Exception{
        final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.S'Z'";
        final SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        final String dateS = "2019-01-07T15:02:52.371Z";
        final Date d = sdf.parse(dateS);
        assertEquals(dateS, sdf.format(d));
    }

    @Test
    public void hardcoded_Z_in_SDF_c() throws Exception{
        final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.S'Z'";
        final SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        final List<String> problematicDates = Arrays.asList(new String[]{"2019-01-07T15:02:52.371"
                                                                         , "2019-01-07T15:02:52.371z"
                                                                         , "2019-01-07T15:02:52.371 z"
                                                                         , "2019-01-07T15:02:52.371 Z"
                                                                         , "2019-01-07T15:02:52.371+00"
                                                                         , "2019-01-07T15:02:52.371 EST"
            });
        for (final String problematicDate: problematicDates) {
            try {
                final Date d = sdf.parse(problematicDate);
                Assert.fail(String.format("was expecting an exception to be thrown when parsing [%s] with mask [%s]"
                                          , problematicDate
                                          , PATTERN));
            } catch (ParseException e) {
            }
        }
    }

    /* This test proves that the time zone configured on the SimpleDateFormat
       instance is ignored if a time zone is also indicated on the String
       to be parsed.
     */
    @Test
    public void parseCase_a() throws Exception {
        final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.S Z";
        final SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone("EST"));
        final String dateS = "2019-01-08T15:30:20.0 +0300";
        final Date date = sdf.parse(dateS);
        final String dateS2 = "2019-01-08T07:30:20.0 -0500";
        assertEquals(dateS2, sdf.format(date));
    }


    /* This test proves that the time zone configured on the SimpleDateFormat
       instance is used when the String to be parsed does not provide a time zone.
       It also demonstrates the use of different SimpleDateFormat instances for
       parsing and formatting.
     */
    @Test
    public void parseCase_b() throws Exception {
        final String PATTERN_FOR_PARSING = "yyyy-MM-dd'T'HH:mm:ss.S";
        final SimpleDateFormat sdfForParsing = new SimpleDateFormat(PATTERN_FOR_PARSING);
        sdfForParsing.setTimeZone(TimeZone.getTimeZone("EST"));

        final String PATTERN_FOR_FORMATTING = "yyyy-MM-dd'T'HH:mm:ss.S Z";
        final SimpleDateFormat sdfForFormatting = new SimpleDateFormat(PATTERN_FOR_FORMATTING);
        sdfForFormatting.setTimeZone(TimeZone.getTimeZone("PST"));
        
        
        final String dateS = "2019-01-08T15:30:20.0";
        final Date date = sdfForParsing.parse(dateS);
        final String dateS2 = "2019-01-08T12:30:20.0 -0800";
        assertEquals(dateS2, sdfForFormatting.format(date));
    }


    /* Demonstrates that parsing and formatting is aware of the DST effects
       on a particular time zone.
     */
    @Test
    public void parseCase_c() throws Exception {

        final TimeZone EST = TimeZone.getTimeZone("EST");

        final TimeZone PST = TimeZone.getTimeZone("PST");

        SimpleDateFormat sdfForParsing = null;
        {
            final String PATTERN_FOR_PARSING = "yyyy-MM-dd'T'HH:mm:ss.S";
            sdfForParsing = new SimpleDateFormat(PATTERN_FOR_PARSING);
            sdfForParsing.setTimeZone(EST);
        }

        SimpleDateFormat sdfForFormatting = null;
        {
            final String PATTERN_FOR_FORMATTING = "yyyy-MM-dd'T'HH:mm:ss.S Z";
            sdfForFormatting = new SimpleDateFormat(PATTERN_FOR_FORMATTING);
            sdfForFormatting.setTimeZone(PST);
        }
        

        // date in Winter (no DST)
        {
            final String dateS = "2019-01-08T15:30:20.0";
            final Date date = sdfForParsing.parse(dateS);
            assertFalse(PST.inDaylightTime(date));
            final String dateS2 = "2019-01-08T12:30:20.0 -0800";
            assertEquals(dateS2, sdfForFormatting.format(date));
        }

        // date in Summer (DST in effect)
        {
            final String dateS = "2019-08-08T15:30:20.0";
            final Date date = sdfForParsing.parse(dateS);
            assertTrue(PST.inDaylightTime(date));
            assertEquals(TimeUnit.HOURS.toMillis(1), PST.getDSTSavings());
            final String dateS2 = "2019-08-08T13:30:20.0 -0700";
            assertEquals(dateS2, sdfForFormatting.format(date));
        }        
    }
    

    @Test
    /*
      This test case proves that if a time zone is not explicitly configured on the SDF object
      (via a call to setTimeZone), then when formatting a Date object, the default time zone is
      used.
     */
    public void formatCase_a() throws Exception{
        final Date d = new Date(1546888371759L); // 2019-01-07 14:13 EST
        
        final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.S Z";
        final SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        final TimeZone defaultTimeZone = TimeZone.getDefault();

        final int offsetMs = defaultTimeZone.getOffset(d.getTime());
        final long offsetHours   = TimeUnit.MILLISECONDS.toHours(offsetMs);
        final long offsetMinutes = TimeUnit.MILLISECONDS.toMinutes(offsetMs-TimeUnit.HOURS.toMillis(offsetHours));
        System.out.printf("offset hours: [%d] minutes: [%s]\n"
                          , offsetHours
                          , offsetMinutes);
        if (offsetMinutes == 0) {
            // we can proceed with this test only if the offset is an integer number of hours
            final String dateS = sdf.format(d);
            final String dateOffsetHHMM = dateS.split(" ")[1];
            assertTrue(dateOffsetHHMM.endsWith("00")); // zero minutes on the hour offset
            final int dateOffsetHours = Integer.parseInt(dateOffsetHHMM)/100; // clip the last two digits
            assertEquals(offsetHours, dateOffsetHours);
        }
    }

    @Test
    /*
      This test case proves that when a time zone is explicitly set on the SDF object, then the stringified
      date reflects the offset when the Z mask is used. It also demonstrates how to do the arithmetic and
      what are the proper invariants that are observed. Note that you have to use getOffset(Date) and not
      getRawOffset() as the latter does not take into account Daylight Savings Time (DST). The test case actually
      fails for many locales in the Southern Hemisphere if you replace the call to getOffset(Date) with a call 
      to getRawOffset(). It makes sense that in such a case failures are detected in Shouthern Hemisphere
      locales only as the date used for the test is summer for that hemisphere so that's where you'd expect
      DST complications to arise.
     */
    public void formatCase_b() throws Exception {
        final Date d = new Date(1546888371759L); // 2019-01-07 14:13 EST
        
        final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.S Z";
        final SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
        for (final String tzId: TimeZone.getAvailableIDs()) {
            final TimeZone tz = TimeZone.getTimeZone(tzId);
            final int offsetMs = tz.getOffset(d.getTime());
            final long offsetHours   = TimeUnit.MILLISECONDS.toHours(offsetMs);
            final long offsetMinutes = TimeUnit.MILLISECONDS.toMinutes(offsetMs-TimeUnit.HOURS.toMillis(offsetHours));
            if (false)
                System.out.printf("Timezone [%20s]: offset hours: [%3d] minutes: [%3d]\n"
                                  , tzId
                                  , offsetHours
                                  , offsetMinutes);
            sdf.setTimeZone(tz);
            final String dateS = sdf.format(d);
            final String dateOffsetHHMM = dateS.split(" ")[1];
            final int sign = dateOffsetHHMM.startsWith("-")?-1:1;
            final String dateOffsetHHMM_hours   = dateOffsetHHMM.substring(0, dateOffsetHHMM.length()-2);
            final String dateOffsetHHMM_minutes = dateOffsetHHMM.substring(dateOffsetHHMM.length()-2);
            final int dateOffsetHHMM_hoursInt   = Integer.parseInt(dateOffsetHHMM_hours);
            final int dateOffsetHHMM_minutesInt = sign*Integer.parseInt(dateOffsetHHMM_minutes);
            assertEquals(String.format("For timezone [%s], I calculated minute offset as [%d] whereas the"
                                       +" date [%s] is formatted with a minute offset of [%d]."
                                       , tzId
                                       , offsetMinutes
                                       , dateS
                                       , dateOffsetHHMM_minutesInt)
                         , offsetMinutes
                         , dateOffsetHHMM_minutesInt);

            assertEquals(String.format("For timezone [%s], I calculated hours offset as [%d] whereas the"
                                       +" date [%s] is formatted with an hours offset of [%d]."
                                       , tzId
                                       , offsetHours
                                       , dateS
                                       , dateOffsetHHMM_hoursInt)
                         , offsetHours
                         , dateOffsetHHMM_hoursInt);
        }
    }    
}


