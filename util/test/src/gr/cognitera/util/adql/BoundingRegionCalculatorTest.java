package gr.cognitera.util.adql;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.net.URL;
import java.util.Objects;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import org.junit.rules.ErrorCollector;
import org.junit.Rule;


import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;

import java.text.DecimalFormat;


public class BoundingRegionCalculatorTest {

    @Test
    public void demonstrateThatClassesAreVisible() {
        final BoundingRegionCalculator x = null;
    }

    @Test
    public void testThatOutOfLimitsAreDetectedForRA() {
        double[] ras = new double[]{-Math.ulp(0), 360+Math.ulp(360)};
        for (double ra: ras) {
            boolean exceptionThrown = false;
            try {
                BoundingRegionCalculator.calculate(ra, 0, 0);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("RA"));
                exceptionThrown = true;
            }
            Assert.assertTrue(String.format("failed to throw exception for RA=[%s]"
                                            , (new DecimalFormat("#.####E0")).format(ra))
                              , exceptionThrown);
        }
    }

    @Test
    public void testThatOutOfLimitsAreDetectedForDEC() {
        double[] decs = new double[]{-90-Math.ulp(-90), 90+Math.ulp(90)};
        for (double dec: decs) {
            boolean exceptionThrown = false;
            try {
                BoundingRegionCalculator.calculate(0, dec, 0);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("DEC"));
                exceptionThrown = true;
            }
            Assert.assertTrue(String.format("failed to throw exception for DEC=[%s]"
                                            , (new DecimalFormat("#.####E0")).format(dec))
                              , exceptionThrown);
        }
    }

    @Test
    public void testThatOutOfLimitsAreDetectedForSR() {
        double[] srs = new double[]{0-Math.ulp(0), 180+Math.ulp(180)};
        for (double sr: srs) {
            boolean exceptionThrown = false;
            try {
                BoundingRegionCalculator.calculate(0, 0, sr);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(e.getMessage().contains("SR"));
                exceptionThrown = true;
            }
            Assert.assertTrue(String.format("failed to throw exception for SR=[%s]"
                                            , (new DecimalFormat("#.####E0")).format(sr))
                              , exceptionThrown);
        }
    }

    @Test
    public void testThatJustBarelyInsideLimitsAreAllowedForRA() {
        double[] ras = new double[]{Math.ulp(0), 360-Math.ulp(360)};
        for (double ra: ras) {
            BoundingRegionCalculator.calculate(ra, 0, 0);
        }
    }

    @Test
    public void testThatJustBarelyInsideLimitsAreAllowedForDEC() {
        double[] decs = new double[]{-90+Math.ulp(-90), 90-Math.ulp(90)};
        for (double dec: decs) {
            BoundingRegionCalculator.calculate(0, dec, 0);
        }
    }

    @Test
    public void testThatJustBarelyInsideLimitsAreAllowedForSR() {
        double[] srs = new double[]{0+Math.ulp(0), 180-Math.ulp(180)};
        for (double sr: srs) {
            BoundingRegionCalculator.calculate(0, 0, sr);
        }
    }
}
