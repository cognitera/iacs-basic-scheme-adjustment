package gr.cognitera.util.time;

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


public class LeapYearUtilTest {

    @Test
    public void demonstrateThatClassesAreVisible() {
        LeapYearUtil x  = null;
    }


    @Test
    public void testCasesA() {
        assertEquals(LeapYearUtil.isLeapYear(4), true);
        assertEquals(LeapYearUtil.isLeapYear(3), false);
        assertEquals(LeapYearUtil.isLeapYear(2), false);
        assertEquals(LeapYearUtil.isLeapYear(1), false);
        assertEquals(LeapYearUtil.isLeapYear(0), true);  // sse-1544736839
        assertEquals(LeapYearUtil.isLeapYear(-1), true); // sse-1544736839
        assertEquals(LeapYearUtil.isLeapYear(-2), false);
        assertEquals(LeapYearUtil.isLeapYear(-3), false);
        assertEquals(LeapYearUtil.isLeapYear(-4), false);        
        assertEquals(LeapYearUtil.isLeapYear(-5), true);
        assertEquals(LeapYearUtil.isLeapYear(-105), true);
        assertEquals(LeapYearUtil.isLeapYear(-405), true);
        assertEquals(LeapYearUtil.isLeapYear(-100), false); // -100 is 99 BC and thus not a leap year in a proleptic Gregorian calendar
    }        
}


/*
  sse-1544736839
  In [https://en.wikipedia.org/wiki/Proleptic_Gregorian_calendar]
  ... we read the following:
    For these calendars one can distinguish two systems of numbering years BC. Bede and later historians did
    not use the Latin zero, nulla, as a year (see 0 (year)), so the year preceding AD 1 is 1 BC. In this
    system the year 1 BC is a leap year (likewise in the proleptic Julian calendar). Mathematically, it is 
    more convenient to include a year 0 and represent earlier years as negative, for the specific purpose
    of facilitating the calculation of the number of years between a negative (BC) year and a positive
    (AD) year. This is the convention used in astronomical year numbering and in the international standard
    date system, ISO 8601. In these systems, the year 0 is a leap year.
 */
