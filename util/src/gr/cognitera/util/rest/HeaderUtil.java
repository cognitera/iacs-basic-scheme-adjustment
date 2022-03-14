package gr.cognitera.util.rest;

import java.util.List;
import java.util.ArrayList;

import org.junit.Assert;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.google.common.base.Joiner;

public final class HeaderUtil {


    public static final List<Header> createHeaders(final String ...keyValues) {
        Assert.assertTrue(String.format("A total of %d key-value pairs were supplied: %s. %d is not an even number!"
                                        , keyValues.length
                                        , Joiner.on(", ").join(keyValues)
                                        , keyValues.length)
                          , keyValues.length %2 == 0);

        final List<Header> rv = new ArrayList<>();
        for (int i = 0; i < keyValues.length / 2; i++) {
            rv.add(new BasicHeader(keyValues[2*i], keyValues[2*i+1]));
        }
        return rv;
    }
}
