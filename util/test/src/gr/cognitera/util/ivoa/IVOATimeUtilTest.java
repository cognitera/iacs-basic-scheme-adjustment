package gr.cognitera.util.ivoa;

import org.junit.Assert;

import org.junit.Test;
import org.junit.Ignore;


import java.util.Date;
import java.util.Random;



public class IVOATimeUtilTest {


    @Test
    public void testStabilityOfTransformations() throws Exception {
        final Random r = new Random(0);
        final int N = 100_000;
        final Date d = new Date();
        for (int i = 0 ; i < N ; i++) {
            final String printed  = IVOATimeUtil.toString(d);
            final Date   d2       = IVOATimeUtil.toDate(printed);
            Assert.assertEquals(d, d2);
            final String printed2 =  IVOATimeUtil.toString(d2);
            Assert.assertEquals(printed, printed2);
            final long randomStep = (long) (r.nextDouble()*100_000);
            d.setTime(d.getTime()+randomStep);
        }
    }        
}


