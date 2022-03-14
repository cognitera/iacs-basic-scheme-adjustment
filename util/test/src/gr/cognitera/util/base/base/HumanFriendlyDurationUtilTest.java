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


public class HumanFriendlyDurationUtilTest {

    @Test
    public void demonstrateThatClassesAreVisible() {
        HumanFriendlyDurationUtil x = null;
    }


    @Test
    public void testJoinWtLastDelim() {
        Assert.assertEquals("0 seconds", HumanFriendlyDurationUtil.toHumanDuration(0));
        Assert.assertEquals("1 second" , HumanFriendlyDurationUtil.toHumanDuration(1));
        Assert.assertEquals("2 seconds", HumanFriendlyDurationUtil.toHumanDuration(2));
        Assert.assertEquals("59 seconds", HumanFriendlyDurationUtil.toHumanDuration(59));
        Assert.assertEquals("1 minute", HumanFriendlyDurationUtil.toHumanDuration(60));
        Assert.assertEquals("1 minute and 1 second", HumanFriendlyDurationUtil.toHumanDuration(61));
        Assert.assertEquals("1 minute and 59 seconds", HumanFriendlyDurationUtil.toHumanDuration(119));
        Assert.assertEquals("2 minutes", HumanFriendlyDurationUtil.toHumanDuration(120));
        Assert.assertEquals("2 minutes and 1 second", HumanFriendlyDurationUtil.toHumanDuration(121));
        Assert.assertEquals("30 minutes", HumanFriendlyDurationUtil.toHumanDuration(1800));
        Assert.assertEquals("59 minutes and 59 seconds", HumanFriendlyDurationUtil.toHumanDuration(3599));
        Assert.assertEquals("1 hour", HumanFriendlyDurationUtil.toHumanDuration(3600));
        Assert.assertEquals("1 hour and 1 second", HumanFriendlyDurationUtil.toHumanDuration(3601));
        Assert.assertEquals("1 hour and 59 seconds", HumanFriendlyDurationUtil.toHumanDuration(3659));
        Assert.assertEquals("1 hour and 1 minute", HumanFriendlyDurationUtil.toHumanDuration(3660));
        Assert.assertEquals("1 hour, 1 minute and 1 second", HumanFriendlyDurationUtil.toHumanDuration(3661));
        Assert.assertEquals("1 hour, 1 minute and 2 seconds", HumanFriendlyDurationUtil.toHumanDuration(3662));
        Assert.assertEquals("1 hour and 2 minutes", HumanFriendlyDurationUtil.toHumanDuration(3720));
        Assert.assertEquals("23 hours, 59 minutes and 59 seconds", HumanFriendlyDurationUtil.toHumanDuration(24*3600-1));
        Assert.assertEquals("1 day", HumanFriendlyDurationUtil.toHumanDuration(24*3600));
        Assert.assertEquals("1 day and 1 second", HumanFriendlyDurationUtil.toHumanDuration(24*3600+1));
    }
}
