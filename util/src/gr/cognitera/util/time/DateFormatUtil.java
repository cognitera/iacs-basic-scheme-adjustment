package gr.cognitera.util.time;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class DateFormatUtil {

    private DateFormatUtil() {}


    public static String format01(final TimeZone tz, final Date d) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        df.setTimeZone(tz);
        return df.format(d);
    }

    public static String format02(final TimeZone tz, final Date d) {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd G 'at' HH:mm:ss.SSSZ (z)");
        df.setTimeZone(tz);
        return df.format(d);
    }    

}

