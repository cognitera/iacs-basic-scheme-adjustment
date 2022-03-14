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


public class UtilTest {

    @Test
    public void demonstrateThatClassesAreVisible() {
        Util x = null;
    }

    private static String toString(byte[] bs) {
        List<String> rv = new ArrayList<>();
        for (byte b: bs) {
            rv.add( String.valueOf( (int) b) );
        }
        return String.format("{%s}", Joiner.on(", ").join(rv));
    }


    @Test
    public void testDatatypeConverterWithRandomByteArrays() {
        Random r = new Random();
        for (int i = 0 ; i < 3000 ; i++) {
            byte[] b = new byte[i];
            r.nextBytes(b);

            String base64  = DatatypeConverter.printBase64Binary(b);
            byte[] b2      = DatatypeConverter.parseBase64Binary(base64);
            if (!Arrays.equals(b, b2))
                throw new RuntimeException();
            String base64B = DatatypeConverter.printBase64Binary(b2);
            if (!base64B.equals(base64))
                throw new RuntimeException();
            if (false)
            System.out.printf("random bytes of length [%d] (%s) calculated as Base64: %s\n"
                              , i
                              , toString(b)
                              , base64);
        }
    }

    @Test
    public void testCastToIntWithMinorLossOfPrecisionSuccessCases() {
        float[] fs = new float[]{1.2f, (float) Math.PI, -23423.24002001f, 989234.0001f, 0.000000002340116e12f, 2147483647f, -2147483648f};
        for (int i = 0; i < fs.length ; i++) {
            int fi = Util.castFloatToInt(fs[i]);
            Assert.assertTrue(Math.abs(fi-fs[i])<1);            
        }
    }

    @Test
    public void testCastToIntWithMinorLossOfPrecisionFailureCases() {
        float[] fs = new float[]{1e32f, -1e32f, 2e36f, -1.06e32f, 2147490000f, -2147490000f};
        for (int i = 0; i < fs.length ; i++) {
            boolean exceptionRaised = false;
            try {
                int fi = Util.castFloatToInt(fs[i]);
                System.out.printf("float cast to int became: %d\n", fi);
            } catch (IllegalArgumentException e) {
                exceptionRaised = true;
            }
            Assert.assertTrue(exceptionRaised);
        }
    }

    @Test
    public void testLosslessCastToIntSuccessCases() {
        long[] ls = new long[]{100l, -23423l, 989234l, 0l, 2147483647l, -2147483648l};
        for (int i = 0; i < ls.length ; i++) {
            int li = Util.castLongToInt(ls[i]);
            Assert.assertEquals(li, ls[i]);
        }
    }

    @Test
    public void testLosslessCastToIntFailureCases() {
        long[] ls = new long[]{2147483648l, -2147483649l};
        for (int i = 0; i < ls.length ; i++) {
            boolean exceptionRaised = false;
            try {
                int li = Util.castLongToInt(ls[i]);
                System.out.printf("long cast to int became: %d\n", li);
            } catch (IllegalArgumentException e) {
                exceptionRaised = true;
            }
            Assert.assertTrue(exceptionRaised);
        }
    }

    @Test
    public void testFloatingPointToIntegerMethods() {
        System.out.printf("Testing method [%s]\n", "Util.fpIsInTheSetZ and company");
        double d = 0.0;
        int counter = 0;
        while (d < 1e15) {
            Assert.assertTrue(Util.fpIsInTheSetZ(d));
            if (d <= 1e9) {
                d++; // exhaustively check all doubles up to a billion
                Assert.assertTrue(Util.fpIsWithinIntegerRange(d));
                Assert.assertTrue(Util.canBeSafelyCastToInt(d));
            } else
                d += Math.floor(counter/10000); // move faster after the first billion
            counter++;
            if (counter<0) {
                System.out.printf("overflow happened\n");
                counter=0;
            }
            if (counter%100000000==0)
                System.out.printf("Reached %22.6f\n", d);
        }
        System.out.printf("Broke out of the loop d = [%22.6f]\n", d);
        System.out.printf("Done testing method [%s]\n", "Util.fpIsInTheSetZ and company");
    }
}


