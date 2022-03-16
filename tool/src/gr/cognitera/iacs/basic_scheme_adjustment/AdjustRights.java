package gr.cognitera.iacs.basic_scheme_adjustment;

import java.util.List;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


import org.junit.Assert;


public class AdjustRights {




    public static void doWork(final RegionalValues rgValConfig
                              , final List<Right> rights
                              , final RightType rightType) {
        final AdjustBelowResults adjustBelowResults = adjustBelowRegional(rgValConfig, rights, rightType);
        System.out.printf("right type: %s # Adjust below results are:\n%s\n"
                          , rightType
                          , adjustBelowResults);

        
        final BigDecimal totalAfterBelowAdjustment = Right.totalValue(rights, rightType);
        Assert.assertEquals(0, adjustBelowResults.finalV.compareTo(totalAfterBelowAdjustment));
        final BigDecimal shortfall2 = totalAfterBelowAdjustment.subtract(adjustBelowResults.initial);
        Assert.assertTrue(shortfall2.compareTo(BigDecimal.ZERO)>0);
        Assert.assertEquals(0, adjustBelowResults.shortFall.compareTo(shortfall2));
        adjustAboveRegional(rgValConfig, rights, rightType, shortfall2);
        final BigDecimal final_value = Right.totalValue(rights, rightType);
    }

    private static AdjustBelowResults adjustBelowRegional(final RegionalValues rgValConfig
                                                  , final List<Right> rights
                                                  , final RightType rightType) {
        final MathContext distanceMC = new MathContext(5, RoundingMode.UNNECESSARY);
        final MathContext sixtypcMC  = new MathContext(5, RoundingMode.HALF_UP);
        final MathContext addMC      = new MathContext(5, RoundingMode.HALF_EVEN);


        
        int adj_once = 0;
        int adj_twice = 0;
        BigDecimal initial = Right.totalValue(rights, rightType);
        BigDecimal shortFall = BigDecimal.ZERO;
        BigDecimal finalV    = BigDecimal.ZERO;
        for (final Right right: rights) {
            if (right.type.equals(rightType)) {
                final BigDecimal applicableRegionalValue = rgValConfig.valueFor(right.type);
                BigDecimal newValue;
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
                    final BigDecimal prevValue= right.totalValue();
                    right.unit_value = new_unit_value;
                    newValue = right.totalValue();
                    final BigDecimal diff = newValue.subtract(prevValue);
                    Assert.assertTrue(diff.compareTo(BigDecimal.ZERO)>0);
                    shortFall = shortFall.add(diff);
                }
                newValue = right.totalValue();
                finalV = finalV.add(newValue);
            }
        }
        return new AdjustBelowResults(initial, adj_once, adj_twice, finalV, shortFall);
    }

    private static void adjustAboveRegional(final RegionalValues rgValConfig
                                            , final List<Right> rights
                                            , final RightType rightType
                                            , final BigDecimal shortFall) {
        final BigDecimal totalAbove = Right.totalValue(rights
                                                       , rightType
                                                       , RightValueSelector.ABOVE
                                                       , rgValConfig.valueFor(rightType));
        if (totalAbove.equals(BigDecimal.ZERO)) {
            System.out.printf("no discount is possible because no rights have a unit value greater than the regional target [%.5f]\n", rgValConfig.valueFor(rightType));
        } else {
            final int DISCOUNT_SCALE = 2;
            final BigDecimal minimumHorizontalDiscount = shortFall.divide(totalAbove
                                                                          , DISCOUNT_SCALE
                                                                          , RoundingMode.CEILING);
            Assert.assertTrue(minimumHorizontalDiscount.compareTo(BigDecimal.ZERO) == 1);
            Assert.assertTrue(minimumHorizontalDiscount.compareTo(new BigDecimal(100)) <= 0);
            Assert.assertEquals(DISCOUNT_SCALE, minimumHorizontalDiscount.scale());
            System.out.printf("minimum horizontal discount calculated as: %.2f\n", minimumHorizontalDiscount);
        }
    }    





}
