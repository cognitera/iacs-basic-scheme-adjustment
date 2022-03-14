package gr.cognitera.util.base;

import com.google.common.base.Function;


public class StringClipper implements Function<String, String> {

    private final int first;
    private final int last;

    public StringClipper(final int first, final int last) {
        this.first = first;
        this.last = last;
    }

    @Override
    public String apply(final String s) {
        if ((first < 0) || (last < 0))
            throw new IllegalArgumentException(String.format("[first] and [last] have to be non-negative; "+
                                                             "instead they were: [first]=[%d], [last]=[%d]"
                                                             , first, last));
        final int len = s.length();
        if (first+last>=len)
            return s;
        else {
            return String.format("{total length: [%d] * first %d chars: [%s] * last %d chars: [%s]}"
                                 , len
                                 , first
                                 , s.substring(0, first)
                                 , last
                                 , s.substring(len-last, len));
        }
    }
}
