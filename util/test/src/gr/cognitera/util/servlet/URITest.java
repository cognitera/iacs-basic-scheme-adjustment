package gr.cognitera.util.servlet;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.everyItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import org.junit.rules.ErrorCollector;
import org.junit.Rule;

import javax.xml.bind.DatatypeConverter;

import com.google.common.base.Joiner;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

import java.io.UnsupportedEncodingException;

class ConstructorSupplier {
    public String uri;
    public String query;
    public String queryRaw;

    public ConstructorSupplier(final String uri, final String query) {
        this(uri, query, query);
    }

    public ConstructorSupplier(final String uri, final String query, final String queryRaw) {
        this.uri = uri;
        this.query = query;
        this.queryRaw = queryRaw;
    }

    public Object[] supplyArguments() {
        return new Object[]{uri, query, queryRaw};
    }

}


@RunWith(Parameterized.class)
public class URITest {

    public final String uriS;
    public final String query;
    public final String queryRaw;


    public URITest(final String uriS, final String query, final String queryRaw) {
        this.uriS     = uriS;
        this.query    = query;
        this.queryRaw = queryRaw;
    }

    private static List<ConstructorSupplier> _data() {
        try {
            List<ConstructorSupplier> rv = new ArrayList<>();
            rv.add(new ConstructorSupplier("http://www.google.com?a=1&b=2", "a=1&b=2"));
            {
                final String someURL = "https://www.foo.com:23?x=23&y=24";
                final String someURLEnc = URLEncoder.encode(someURL, StandardCharsets.UTF_8.name());
                final String URL = String.format("https://www.example.com:2414?a=%s&b=2", someURLEnc);
                rv.add(new ConstructorSupplier(URL, String.format("a=%s&b=2", someURL)
                                                  , String.format("a=%s&b=2", someURLEnc)));
            }
            rv.add(new ConstructorSupplier("www.example.com?a=foo%3Fb%3D2&c=3", "a=foo?b=2&c=3", "a=foo%3Fb%3D2&c=3"));
            rv.add(new ConstructorSupplier("www.example.com?a=www%2Eexample%2Ecom%3Fb%3D2&c=3", "a=www.example.com?b=2&c=3", "a=www%2Eexample%2Ecom%3Fb%3D2&c=3"));
            rv.add(new ConstructorSupplier("www.example.com?url=www%2Eotherexample%2Ecom%3Fb%3D2%26c%3D3&d=4", "url=www.otherexample.com?b=2&c=3&d=4", "url=www%2Eotherexample%2Ecom%3Fb%3D2%26c%3D3&d=4"));
            String nextStageComplexURL = "www.example.com?url=www%2Eotherexample%2Ecom%3Fb%3D2%26c%3D3&d=4";
            final int A_LARGE_INTEGER_FOR_RIDICULOUSLY_COMPLEX_URLS = 100;
            for (int i = 0; i < A_LARGE_INTEGER_FOR_RIDICULOUSLY_COMPLEX_URLS; i++) {
                final String someURL    = nextStageComplexURL;
                final String someURLEnc = URLEncoder.encode(someURL, StandardCharsets.UTF_8.name());
                final String URL = String.format("https://www.foo.com:1061?a=1&b=2&c=%s", someURLEnc);
                nextStageComplexURL = URL;
                rv.add(new ConstructorSupplier(URL
                                               , String.format("a=1&b=2&c=%s", someURL)
                                               , String.format("a=1&b=2&c=%s", someURLEnc)));
            }
            return rv;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Parameters(name = "{index}: {0} ({1})}")
    public static Collection<Object[]> data() {
        System.out.printf("\n\n\nFor the [%s] test see:\n\n\t\t\thttps://stackoverflow.com/q/48776437/274677\n\n\n", URITest.class);
        List<Object[]> rv = new ArrayList<>();        
        for (ConstructorSupplier x: _data())
            rv.add(x.supplyArguments());
        return rv;
    }
    
    @Test
    public void queryPortionsAreExtractedAsExpected() throws URISyntaxException {
        final URI uri = new URI(this.uriS);
        Assert.assertEquals(this.query   , uri.getQuery());
        Assert.assertEquals(this.queryRaw, uri.getRawQuery());
    }
}



