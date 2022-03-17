package gr.cognitera.iacs.basic_scheme_adjustment;

import java.util.List;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


import org.junit.Assert;


public class AdjustRights {

    private static final String EUR = "\u20ac";

    private static Logger logger = Logger.INSTANCE;

    public static void doWork(final RegionalValues rgValConfig
                              , final List<Right> rights
                              , final RightType rightType) {
        final AdjustBelowResults adjustBelowResults = adjustBelowRegional(rgValConfig, rights, rightType);
        logger.info("right type: [%s] raises # Initial TVBRV: %.2f%s %d rights raised by 1/3 of distance %d rights further raised to reach 60%, final TVBRV %2.f%s, shortfall %.2f%s\n"
                    , rightType.name
                    , adjustBelowResults.initial
                    , EUR
                    , adjustBelowResults.rightsRaised
                    , adjustBelowResults.rightsFurtherRaised
                    , adjustBelowResults.finalV
                    , EUR
                    , adjustBelowResults.shortFall
                    , EUR);

        
        final BigDecimal totalAfterBelowAdjustment = Right.totalValue(rights, rightType);
        Assert.assertEquals(0, adjustBelowResults.finalV.compareTo(totalAfterBelowAdjustment));
        final BigDecimal shortfall2 = totalAfterBelowAdjustment.subtract(adjustBelowResults.initial);
        Assert.assertTrue(shortfall2.compareTo(BigDecimal.ZERO)>0);
        Assert.assertEquals(0, adjustBelowResults.shortFall.compareTo(shortfall2));
        final FinalDiscountResults discount = adjustAboveRegional(rgValConfig, rights, rightType, shortfall2);
        logger.info("right type %s # adjust above results are:\n%s\n"
                    , rightType
                    , discount);
        logger.info("right type %s # after adjustment over %d rounds, surplus is %.2f (previous round surplus was: %.2f)\n"
                    , rightType
                    , discount.rounds
                    , discount.surplus(shortfall2)
                    , discount.previousSurplus(shortfall2));
        logger.info("right type: [%s] reductions # Initial value: %.2f%s %d rights raised by 1/3 of distance %d rights further raised to reach 60%, final value %2.f%s, shortfall %.2f%s\n"
                    ); // TODO
        final BigDecimal final_value = Right.totalValue(rights, rightType);
    }

    private static AdjustBelowResults adjustBelowRegional(final RegionalValues rgValConfig
                                                  , final List<Right> rights
                                                  , final RightType rightType) {


        
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
                    final BigDecimal initialUV = right.unit_value.add(BigDecimal.ZERO);
                    final BigDecimal distance = applicableRegionalValue.subtract(initialUV);
                    Assert.assertTrue(distance.compareTo(BigDecimal.ZERO)>0);
                    final BigDecimal third = distance.divide(new BigDecimal(3), RoundingMode.HALF_UP);
                    BigDecimal new_unit_value = initialUV.add(third);
                    final BigDecimal sixtypc = applicableRegionalValue.multiply(new BigDecimal(0.6));
                    if (new_unit_value.compareTo(sixtypc)<0) {
                        adj_twice++;
                        new_unit_value = sixtypc;
                    }
                    new_unit_value = new_unit_value.setScale(3, RoundingMode.HALF_UP);
                    final BigDecimal prevValue= right.totalValue();
                    right.unit_value = new_unit_value;
                    newValue = right.totalValue();
                    final BigDecimal diff = newValue.subtract(prevValue);
                    final String MSG = String.format("righttype %s # initialUV: %.5f"
                                                     +" regional: %.5f distance: %.5f"
                                                     +" third: %.5f 60%%: %.5f new: %.5f"
                                                     +" prev total: %.5f new total: %.5f"
                                                     +" diff: %.5f"
                                      , rightType
                                      , initialUV
                                      , applicableRegionalValue
                                      , distance
                                      , third
                                      , sixtypc
                                      , new_unit_value
                                      , prevValue
                                      , newValue
                                      , diff);
                    Assert.assertTrue(MSG, diff.compareTo(BigDecimal.ZERO)>=0);
                    shortFall = shortFall.add(diff);
                }
                newValue = right.totalValue();
                finalV = finalV.add(newValue);
            }
        }
        return new AdjustBelowResults(initial, adj_once, adj_twice, finalV, shortFall);
    }

    private static FinalDiscountResults adjustAboveRegional(final RegionalValues rgValConfig
                                                            , final List<Right> rights
                                                            , final RightType rightType
                                                            , final BigDecimal shortFall) {
        final BigDecimal totalAbove = Right.totalValue(rights
                                                       , rightType
                                                       , RightValueSelector.ABOVE
                                                       , rgValConfig.valueFor(rightType));
        final BigDecimal minimumHorizontalDiscount = calcMinimumHorizontalDiscount(totalAbove, shortFall);
        logger.debug("minimum horizontal discount calculated as: %.5f\n", minimumHorizontalDiscount);
        BigDecimal prevShortFallRecovered = null;
        TentativeDiscountResults tentative = null;
        int rounds = 0;
        for (BigDecimal discount = minimumHorizontalDiscount;
             discount.compareTo(maxDiscount) <= 0;
             discount = discount.add(new BigDecimal(Math.pow(10, -DISCOUNT_SCALE)))) {
            rounds ++;
            if (rounds > 1)
                prevShortFallRecovered = tentative.shortFallRecovered;
            tentative = tentativelyApplyDiscount(rgValConfig, rights, rightType, discount);
            final BigDecimal diff = shortFall.subtract(tentative.shortFallRecovered);
            logger.trace("Applying a discount of %.5f I was able to recover %.5f out of %.5f (missing %.5f)\n"
                         , discount
                         , tentative.shortFallRecovered
                         , shortFall
                         , diff);

            if (diff.compareTo(BigDecimal.ZERO)<=0)
                break;
        }
        Assert.assertNotNull(tentative);
        return new FinalDiscountResults(rounds, tentative.rights, tentative.shortFallRecovered, prevShortFallRecovered);
    }

    private static TentativeDiscountResults tentativelyApplyDiscount(final RegionalValues rgValConfig
                                                                     , final List<Right> _rights
                                                                     , final RightType rightType
                                                                     , final BigDecimal discount) {
        final List<Right> rights = Right.copy(_rights);
        BigDecimal shortFallRecovered = BigDecimal.ZERO;
        for (final Right r: rights) {
            if (r.type.equals(rightType) && (r.unit_value.compareTo(rgValConfig.valueFor(rightType))>0)) {
                final BigDecimal prevValue = r.totalValue();
                BigDecimal newUnitValue = Util.mulWithScale(r.unit_value
                                                            , BigDecimal.ONE.subtract(discount)
                                                            , 3
                                                            , RoundingMode.HALF_UP);
                if (newUnitValue.compareTo(rgValConfig.valueFor(rightType))<0) {
                    newUnitValue = rgValConfig.valueFor(rightType);
                }
                r.unit_value = newUnitValue;
                final BigDecimal newValue = r.totalValue();
                final BigDecimal diff = prevValue.subtract(newValue);
                Assert.assertTrue(diff.compareTo(BigDecimal.ZERO)>=0);
                shortFallRecovered = shortFallRecovered.add(diff);
            }
        }
        return new TentativeDiscountResults(rights, shortFallRecovered);
    }

    private static BigDecimal calcMinimumHorizontalDiscount(final BigDecimal totalAbove
                                                            , final BigDecimal shortFall) {

        if (totalAbove.equals(BigDecimal.ZERO)) {
            return maxDiscount;
        } else {

            final BigDecimal minimumHorizontalDiscount = shortFall.divide(totalAbove
                                                                          , DISCOUNT_SCALE
                                                                          , RoundingMode.CEILING);
            Assert.assertTrue(minimumHorizontalDiscount.compareTo(BigDecimal.ZERO) == 1);
            Assert.assertEquals(DISCOUNT_SCALE, minimumHorizontalDiscount.scale());
            return maxDiscount.min(minimumHorizontalDiscount);
        }
    }


    private static final BigDecimal maxDiscount = new BigDecimal(0.3);

    private static final int DISCOUNT_SCALE = 4;
}


