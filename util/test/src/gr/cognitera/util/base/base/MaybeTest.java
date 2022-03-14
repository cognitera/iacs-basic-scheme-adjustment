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


public class MaybeTest {

    @Test
    public void testEquals_1() {
        Maybe<String> a1 = Maybe.of("alpha");
        Maybe<String> a2 = Maybe.of("alpha");        
        Assert.assertFalse(a1==a2);
        Assert.assertTrue(a1.equals(a2));
        Assert.assertTrue(a2.equals(a1));
    }

    @Test
    public void testEquals_2() {
        Maybe<String> a1 = Maybe.of((String) null);
        Maybe<String> a2 = Maybe.of((String) null);
        Assert.assertFalse(a1==a2);
        Assert.assertTrue(a1.equals(a2));
        Assert.assertTrue(a2.equals(a1));
    }    
}


