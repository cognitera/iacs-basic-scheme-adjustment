package gr.cognitera.util.ivoa;

import java.util.Date;
import java.util.TimeZone;

import java.text.SimpleDateFormat;
import java.text.ParseException;


public final class IVOATimeUtil {

    /* Note that the Zulu time zone is hardcoded: that's the only time zone we accept and the only one we print for.
       Moreover, I can't use either of the below Java masks:
       - Z : as that prints: "+0000"
       - z : -- ---- ------: "UTC"
       ... therefore I am only left with the option of hardcoding the 'Z'


       Actually, I could also use the 'X' mask and specify the pattern as:

           public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SX";

       ... as that automatically produces the 'Z' at the end iff the time zone on the
       SimpleDateFormat instance is set to "Zulu" via a call to setTimeZone(TimeZone.getTimeZone("Zulu"))
       but that would work only for formatting, not parsing and so I decided to avoid the complication
       and nuances of having two different patterns (one for formatting, the other for parsing) and just
       stick to hardcoding the 'Z'.
    */
    public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.S'Z'";


    private IVOATimeUtil() {}

    /* We can't have this is a static field; see the following Sun Bugs:
       - https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6231579
       - https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6178997
    */
    private static SimpleDateFormat getIVOADateFormat() {
        final SimpleDateFormat rv = new SimpleDateFormat(PATTERN);
        final TimeZone tz = TimeZone.getTimeZone("Zulu");
        rv.setTimeZone(tz);
        return rv;
    }


    public static boolean isValidFormat(final String s) {
        try {
            toDate(s);
            return true;
        } catch (ParseException e) {
            return false;
        }

    }


    public static Date toDate(final String s) throws ParseException {
        return getIVOADateFormat().parse(s);
    }

    public static String toString(final Date date) {
        return getIVOADateFormat().format(date);
    }


}
