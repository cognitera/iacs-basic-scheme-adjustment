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
import java.util.function.Function;
import java.net.URL;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import org.junit.rules.ErrorCollector;
import org.junit.Rule;

import javax.xml.bind.DatatypeConverter;

import com.google.common.base.Joiner;
import java.sql.Timestamp;

public class CollectionDifferenceUtilTest {

    @Test
    public void demonstrateThatClassesAreVisible() {
        CollectionDifferenceUtil x = null;
    }

    @Test
    public void test_000() {
        final List<Integer> a = new ArrayList<Integer>(Arrays.asList(1,2,3,5,8,13,21));
        final List<Integer> b = new ArrayList<Integer>(Arrays.asList(32,5,8,13,21, 55));
        final CollectionDifference EXPECTED = new CollectionDifference(new ArrayList<Integer>(Arrays.asList(1, 2, 3))
                                                                       , new ArrayList<Integer>(Arrays.asList(32, 55))
                                                                       , new ArrayList<Integer>(Arrays.asList(5, 8, 13, 21)));
        final CollectionDifference<Integer, Integer, Integer> ACTUAL
            = CollectionDifferenceUtil.computeDifference(a
                                                         , b
                                                         , Function.identity()
                                                         , Function.identity()
                                                         , Function.identity()
                                                         , Function.identity()
                                                         , Function.identity());
        Assert.assertEquals(EXPECTED, ACTUAL);
    }
}
