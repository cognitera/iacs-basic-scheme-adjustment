package gr.cognitera.util.astro;

import org.junit.Assert;

import org.junit.Test;

import java.util.List;
import java.util.ArrayList;


import gr.cognitera.util.base.Triad;

public class DegreesUtilTest {

    @Test
    public void testClassIsVisible() {
        final DegreesUtil x = null;
    }


    @Test
    public void testRightAscensionToHourMeasure() {
        List<Triad<Double, Integer, String>> testCases = new ArrayList<>();
        testCases.add(Triad.create( 0.0, 2, "00 00 00.00"));
        testCases.add(Triad.create( 0.0, 3, "00 00 00.000"));
        testCases.add(Triad.create( 0.0, 4, "00 00 00.0000"));
        testCases.add(Triad.create(90.0, 5, "06 00 00.00000"));
        testCases.add(Triad.create( 1.0, 2, "00 04 00.00"));
        testCases.add(Triad.create( 5.0, 2, "00 20 00.00"));
        testCases.add(Triad.create(50.0, 3, "03 20 00.000"));
        for (final Triad<Double, Integer, String> testCase: testCases) {
            final String s = DegreesUtil.rightAscensionToHourMeasure(testCase.a, testCase.b);
            Assert.assertEquals(testCase.c, s);
        }
    }

    @Test
    public void testDeclinationToDegreeMeasure() {
        List<Triad<Double, Integer, String>> testCases = new ArrayList<>();
        testCases.add(Triad.create( 0.0     , 2, "+00 00 00.00"));
        testCases.add(Triad.create( 60.23107, 0, "+60 13 52"));        
        testCases.add(Triad.create( 60.23107, 1, "+60 13 51.9"));
        testCases.add(Triad.create( 60.23107, 2, "+60 13 51.85"));
        testCases.add(Triad.create( 60.23107, 3, "+60 13 51.852"));
        testCases.add(Triad.create( 60.23107, 4, "+60 13 51.8520"));
        testCases.add(Triad.create(-60.23107, 0, "-60 13 52"));        
        testCases.add(Triad.create(-60.23107, 1, "-60 13 51.9"));
        testCases.add(Triad.create(-60.23107, 2, "-60 13 51.85"));
        testCases.add(Triad.create(-60.23107, 3, "-60 13 51.852"));
        testCases.add(Triad.create(-60.23107, 4, "-60 13 51.8520"));        
        for (final Triad<Double, Integer, String> testCase: testCases) {
            final String s = DegreesUtil.declinationToDegreesMeasure(testCase.a, testCase.b);
            Assert.assertEquals(testCase.c, s);
        }
    }    
}
