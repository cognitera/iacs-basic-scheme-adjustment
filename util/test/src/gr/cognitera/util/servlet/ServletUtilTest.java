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

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.net.URL;

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

import gr.cognitera.util.base.RandomStringGenerator;


public class ServletUtilTest {
    @Test
    public void removeParameterFromURITest1() {
        final String URL_BASE = "http://localhost:8082/cxcaccount/edit-account.do?urlToGotoAfterEditName=CPS&urlToGotoAfterEdit=https%3A%2F%2Fcxc-dmz-test.cfa.harvard.edu%2Fcps-app%2Flogin%3FisDDT%3DN";
        final String URL = String.format("%s&%s"
                                         , URL_BASE
                                         , "serviceTicket=bfrwz9yb0q2pgprzrr2h0l4e73eh89u67o00047");
        final String actual1 = ServletUtil.removeParameterFromURI(URL, "serviceTicket", true, 1);
        final String actual2 = ServletUtil.removeParameterFromURI(URL, "serviceTicket", false, null);        
        final String expected = URL_BASE;
        Assert.assertEquals(String.format("%s\n is not equal to:\n%s\n... as expected", actual1, expected)
                            , expected
                            , actual1);
        Assert.assertEquals(String.format("%s\n is not equal to:\n%s\n... as expected", actual2, expected)
                            , expected
                            , actual2);
    }

    @Test
    public void removeParameterFromURITest2() {
        final String URL_BASE = "https://example.com/a/b/c";
        final String URL = String.format("%s?%s"
                                         , URL_BASE
                                         , "a=alpha&b=beta&c=gamma");
        final String actual = ServletUtil.removeParameterFromURI(URL, "b", true, 1);
        final String expected = String.format("%s?%s"
                                              , URL_BASE
                                              , "a=alpha&c=gamma");
        Assert.assertEquals(String.format("%s\n is not equal to:\n%s\n... as expected", actual, expected)
                            , expected
                            , actual);
    }


    @Test
    public void removeParameterFromURITest3() {
        final String URL_BASE = "https://example.com/a/b/c";
        final String URL = String.format("%s?%s"
                                         , URL_BASE
                                         , "a=alpha&b=beta&c=gamma&b=beta2&d=delta&b=beta3");
        final String actual = ServletUtil.removeParameterFromURI(URL, "b", true, 3);
        final String expected = String.format("%s?%s"
                                              , URL_BASE
                                              , "a=alpha&c=gamma&d=delta");
        Assert.assertEquals(String.format("%s\n is not equal to:\n%s\n... as expected", actual, expected)
                            , expected
                            , actual);
    }

}


