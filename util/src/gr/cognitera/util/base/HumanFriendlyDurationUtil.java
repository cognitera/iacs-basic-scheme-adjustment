package gr.cognitera.util.base;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Arrays;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.junit.Assert;

import com.google.common.base.Joiner;

public class HumanFriendlyDurationUtil {

    private HumanFriendlyDurationUtil() {}
    public static String toHumanDuration(final int seconds) {
        Assert.assertTrue(seconds>=0);
        final int minutes          = seconds / 60;
        final int secondsRemainder = seconds - minutes*60;
        final int hours            = minutes / 60;
        final int minutesRemainder = minutes - hours*60;
        final int days             =   hours / 24;
        final int hoursRemainder   =   hours - days*24;
        List<String> rv = new ArrayList<>();
        if (days>0)
            rv.add(singularOrPlural(days, "day"));
        if (hoursRemainder>0)
            rv.add(singularOrPlural(hoursRemainder, "hour"));
        if (minutesRemainder>0)
            rv.add(singularOrPlural(minutesRemainder, "minute"));
        if (secondsRemainder>0 || rv.size()==0)
            rv.add(singularOrPlural(secondsRemainder, "second"));
        String rvS =  StringUtil.joinWtLastDelim(rv, ", ", " and ");
        System.out.printf("returning: [%s]\n", rvS);
        return rvS;
    }

    private static String singularOrPlural(final int n, final String unit) {
        if (n==1)
            return String.format("1 %s", unit);
        else
            return String.format("%d %ss", n, unit);
    }    
}
