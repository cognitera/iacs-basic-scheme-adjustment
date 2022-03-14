package gr.cognitera.util.time;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;


/**
 * Represents a year-month-day value with no time zone information in friendly, civilian format
 *
 */
public class YearMonthDay {

    /**
     * the year - there's no year 0; the year before +1 (1 AD) is represented as -1 (1 BC)
     */
    public final int      year;
    /**
     * the month (January is 1)
     */
    public final int      month;
    /**
     * the day of the month (first day is 1)
     */
    public final int      day;

    public YearMonthDay(final int year,
                        final int month,
                        final int day) {
        if (year==0)
            throw new IllegalArgumentException("there's no year 0; the year before +1 (1 AD) is represented as -1 (1 BC)");
        this.year  = year;
        if ((month<=0) || (month>=13))
            throw new IllegalArgumentException(String.format("month value of [%d] is not allowed;"
                                                             +" months are 1-indexed;"
                                                             +" therefore, valid month range is [1, 12]"
                                                             , month));
        this.month = month;
        if ((month<=0) || (month>=32))
            throw new IllegalArgumentException(String.format("day value of [%d] is not allowed;"
                                                             +" days are 1-indexed;"
                                                             +" therefore, valid day range is [1, 31]"
                                                             , month));        
        this.day   = day;
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("year", year)
            .add("month", month)
            .add("day", day)
            ;
    }
    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        
        if (!(o instanceof YearMonthDay))
            return false;

        final YearMonthDay other = (YearMonthDay) o;
        return Objects.equals(this.year , other.year)
            && Objects.equals(this.month, other.month)
            && Objects.equals(this.day  , other.day);
    }

    @Override
    public int hashCode(){
        return Objects.hash(year, month, day);
    }
    
    
}
