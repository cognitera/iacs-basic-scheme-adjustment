package gr.cognitera.util.time;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;


/**
 * Represents a year-month-day value with no time zone information in friendly, civilian format
 *
 */
public class YearMonthDayHourMinSecMil extends YearMonthDay {

    public final int      hour;
    public final int      minute;
    public final int      second;
    public final int      millisecond;

    public YearMonthDayHourMinSecMil(final int year,
                                     final int month,
                                     final int day,
                                     final int hour,
                                     final int minute,
                                     final int second,
                                     final int millisecond) {
        super(year, month, day);
        this.hour        = hour;
        this.minute      = minute;
        this.second      = second;
        this.millisecond = millisecond;
    }

    public YearMonthDayHourMinSecMil(final YearMonthDay yearMonthDay,
                                     final int hour,
                                     final int minute,
                                     final int second,
                                     final int millisecond) {
        this(yearMonthDay.year,
             yearMonthDay.month,
             yearMonthDay.day,
             hour,
             minute,
             second,
             millisecond);
    }

    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
            .add("hour", hour)
            .add("minute", minute)
            .add("second", second)
            .add("millisecond", millisecond)
            ;
    }
    @Override
    public final String toString() {
        return toStringHelper().toString();
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null) return false;

        if (!(o instanceof YearMonthDayHourMinSecMil))
            return false;

        final YearMonthDayHourMinSecMil other = (YearMonthDayHourMinSecMil) o;
        return super.equals(other)
            && Objects.equals(this.hour       , other.hour)
            && Objects.equals(this.minute     , other.minute)
            && Objects.equals(this.second     , other.second)
            && Objects.equals(this.millisecond, other.millisecond);
    }

    @Override
    public int hashCode(){
        return Objects.hash(super.hashCode(), hour, minute, second, millisecond);
    }

    public YearMonthDay toYearMonthDay(final boolean lossless) {
        if (lossless && ((hour!=0) || (minute!=0) && (second!=0) && (millisecond!=0)))
            throw new IllegalStateException(String.format("fields smaller than the day are not all 0 in [%s]"
                                                          , this));
        else
            return new YearMonthDay(this.year, this.month, this.day);
    }

    public static YearMonthDayHourMinSecMil earliest(final YearMonthDay d) {
        return new YearMonthDayHourMinSecMil(d, 0, 0, 0, 0);
    }

    public static YearMonthDayHourMinSecMil latest(final YearMonthDay d) {
        return new YearMonthDayHourMinSecMil(d, 23, 59, 59, 999);
    }

}
