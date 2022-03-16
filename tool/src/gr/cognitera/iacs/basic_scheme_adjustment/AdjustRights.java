package gr.cognitera.iacs.basic_scheme_adjustment;

import java.util.List;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


import org.junit.Assert;

public class AdjustRights {




    public static List<Right> doWork(final RegionalValues rgValConfig
                                     , final List<Right> rights) {
        final List<Right> rv = Right.copy(rights);
        adjustBelowRegional(rgValConfig, rv);
        adjustAboveRegional(rgValConfig, rv);

        return rv;

    }

    private static void adjustBelowRegional(final RegionalValues rgValConfig
                                     , final List<Right> rights) {
        final MathContext distanceMC = new MathContext(5, RoundingMode.UNNECESSARY);
        final MathContext sixtypcMC  = new MathContext(5, RoundingMode.HALF_UP);
        final MathContext addMC      = new MathContext(5, RoundingMode.HALF_EVEN);
        int adj_once = 0;
        int adj_twice = 0;
        for (final Right right: rights) {
            final BigDecimal applicableRegionalValue = rgValConfig.valueFor(right.type);
            if (right.unit_value.compareTo(applicableRegionalValue)<0) {
                adj_once++;
                final BigDecimal distance = applicableRegionalValue.subtract(right.unit_value);
                Assert.assertTrue(distance.compareTo(BigDecimal.ZERO)>0);
                final BigDecimal third = distance.divide(distance, RoundingMode.HALF_UP);
                BigDecimal new_unit_value = right.unit_value.add(third, addMC);
                final BigDecimal sixtypc = applicableRegionalValue.multiply(new BigDecimal(0.6), sixtypcMC);
                if (new_unit_value.compareTo(sixtypc)<0) {
                    adj_twice++;
                    new_unit_value = sixtypc;
                }
                right.unit_value = new_unit_value; 
            }
        }
        System.out.printf("%d rights below average, %d did not reach 60%%\n", adj_once , adj_twice);
    }

    private static void adjustAboveRegional(final RegionalValues rgValConfig
                                     , final List<Right> rights) {

    }    





}
