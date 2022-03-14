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
import java.sql.Timestamp;

public class TimestampUtilTest {

    @Test
    public void demonstrateThatClassesAreVisible() {
        TimestampUtil x = null;
    }

    @Test
    public void testCreate() {
        for (int i = 0 ; i < 1000*1000; i+=2347) {
            long ms = Util.now();
            int msRemainder = Util.castLongToInt(ms % 1000);
            Timestamp t = TimestampUtil.create(ms, i);
            assertEquals(         ms, t.getTime());
            assertEquals(msRemainder*1000*1000+i, t.getNanos());
            assertEquals(i, TimestampUtil.getMillionthsOfMs(t));
        }
    }
}

