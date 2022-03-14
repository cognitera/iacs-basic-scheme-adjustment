package gr.cognitera.util.adql;

import com.google.common.collect.Range;

public class RangeUtil {

    public static final String rangeCondition(final String columnName, final Range<Double> range) {
        return String.format("%s>=%f AND %s<=%f", columnName, range.lowerEndpoint(), columnName, range.upperEndpoint());
    }
}
