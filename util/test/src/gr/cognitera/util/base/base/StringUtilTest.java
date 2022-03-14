package gr.cognitera.util.base;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;

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

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Random;
import java.net.URL;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import org.junit.rules.ErrorCollector;
import org.junit.Rule;

import javax.xml.bind.DatatypeConverter;

import com.google.common.base.Joiner;


public class StringUtilTest {

    @Test
    public void demonstrateThatClassesAreVisible() {
        StringUtil x = null;
    }


    @Test
    public void testJoinWtLastDelim() {
        Assert.assertEquals(null         , StringUtil.joinWtLastDelim(new ArrayList<String>(), "foo", "boo"));
        Assert.assertEquals("a"          , StringUtil.joinWtLastDelim(Arrays.asList("a"), "foo", "boo"));
        Assert.assertEquals("a and b"    , StringUtil.joinWtLastDelim(Arrays.asList("a", "b"), "foo", " and "));
        Assert.assertEquals("a, b, c and d"    , StringUtil.joinWtLastDelim(Arrays.asList("a", "b", "c", "d"), ", ", " and "));        
    }

    @Test
    public void testIsNeitherEmptyStringNorWhitespaceButMaybeNull() {
        Assert.assertEquals(true, StringUtil.isNeitherEmptyStringNorWhitespaceButMaybeNull(null));
        Assert.assertEquals(true, StringUtil.isNeitherEmptyStringNorWhitespaceButMaybeNull("foo"));
        Assert.assertEquals(true, StringUtil.isNeitherEmptyStringNorWhitespaceButMaybeNull("   foo   bar  "));
        Assert.assertEquals(false, StringUtil.isNeitherEmptyStringNorWhitespaceButMaybeNull(""));
        Assert.assertEquals(false, StringUtil.isNeitherEmptyStringNorWhitespaceButMaybeNull("   "));
        Assert.assertFalse(StringUtil.isNeitherEmptyStringNorWhitespaceButMaybeNull(" \n\n \t\t  \t\t \n  "));
    }

    @Test
    public void testIsNumber() {
        Assert.assertEquals(true, StringUtil.isNumber("0"));
        Assert.assertEquals(true, StringUtil.isNumber("-1"));
        Assert.assertEquals(true, StringUtil.isNumber("1"));
        Assert.assertEquals(true, StringUtil.isNumber("42"));
        Assert.assertEquals(true, StringUtil.isNumber("42.0001"));
        Assert.assertEquals(true, StringUtil.isNumber("-42.0001"));

        Assert.assertEquals(false, StringUtil.isNumber("03.a"));
        Assert.assertEquals(false, StringUtil.isNumber("-1.3.3"));
        Assert.assertEquals(false, StringUtil.isNumber("1,3.3"));
        Assert.assertEquals(false, StringUtil.isNumber("42a"));
        Assert.assertEquals(false, StringUtil.isNumber("foo"));
        Assert.assertEquals(false, StringUtil.isNumber(""));        
    }

    @Test
    public void testClipIfNecessary() {
        final String s = "abcdefghijklmnopqrstuvwxyz";
        final int LEN = s.length();
        Assert.assertEquals(26, LEN);
        Assert.assertEquals(s, StringUtil.clipIfNecessary(s, LEN, 0));
        for (int first = 0; first <= 40; first++) {
            final int last = Math.max(LEN-first, 0);
            Assert.assertEquals(s, StringUtil.clipIfNecessary(s, first, last));
        }
        Assert.assertEquals(String.format("{total length: [%d] * first %d chars: [%s] * last %d chars: [%s]}"
                                          , LEN
                                          , 2
                                          , "ab"
                                          , 0
                                          , "")
                            , StringUtil.clipIfNecessary(s, 2, 0));

        Assert.assertEquals(String.format("{total length: [%d] * first %d chars: [%s] * last %d chars: [%s]}"
                                          , LEN
                                          , 2
                                          , "ab"
                                          , 3
                                          , "xyz")
                            , StringUtil.clipIfNecessary(s, 2, 3));
    }

    @Test
    public void testMapify() {
        final List<Triad<String, MapificationConfig, Map<String, String>>> testCases = new ArrayList<>();
        {
            final MapificationConfig mc = new MapificationConfig(";"
                                                                 , "="
                                                                 , MapificationConfig.DuplicateKeyHandling.DUPL_KEYS_NOT_ALLOWED);
            final Map<String, String> m = new LinkedHashMap<>();
            m.put("a", "1");
            m.put("b", "2");

            testCases.add(Triad.create("a=1;b=2", mc, m));
        }
        {
            final Map<String, String> m = new LinkedHashMap<>();
            m.put("a", "1");
            m.put("b", "2");

            testCases.add(Triad.create("a=1;b=2", (MapificationConfig) null, m));
        }
        for (final Triad<String, MapificationConfig, Map<String, String>> testCase: testCases) {
            Map<String, String> map = null;
            if (testCase.b!=null)
                map = StringUtil.mapify(testCase.a, testCase.b);
            else
                map = StringUtil.mapify(testCase.a);
            Assert.assertNotNull(map);
            Assert.assertEquals(testCase.c, map);
        }
    }
}
