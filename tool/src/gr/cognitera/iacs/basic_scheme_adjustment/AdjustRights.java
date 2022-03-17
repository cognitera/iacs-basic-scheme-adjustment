package gr.cognitera.iacs.basic_scheme_adjustment;

import java.util.List;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


import org.junit.Assert;


public class AdjustRights {

    private static final String EUR = "\u20ac";

    private static Logger logger = Logger.INSTANCE;

    public static List<Right> doWork(final RegionalValues rgValConfig
                                     , final List<Right> rights
                                     , final RightType rightType) {
        final RightStats stats1 = Right.computeStats(rights, rightType);
        final AdjustBelowResults adjustBelowResults = adjustBelowRegional(rgValConfig, rights, rightType);
        logger.info("right type: [%s] raises    "
                    +" | AR: %d records %.2f rights %.2f%s"
                    +" | RBRV %d records (%.2f rights)"
                    +" \u2014 of which %d records (%.2f rights) were further raised (beyond 1/3 of the distance) to reach 60%%"
                    +" | AR: %.2f%s"
                    +" | deficit: %.2f%s\n"
                    , rightType.name
                    , adjustBelowResults.allRecords
                    , adjustBelowResults.allRights
                    , adjustBelowResults.allRightsValue
                    , EUR
                    , adjustBelowResults.recordsRaised
                    , adjustBelowResults.rightsRaised
                    , adjustBelowResults.recordsFurtherRaised
                    , adjustBelowResults.rightsFurtherRaised
                    , adjustBelowResults.finalAllRightsValue
                    , EUR
                    , adjustBelowResults.deficit
                    , EUR);

        
        final RightStats stats2 = Right.computeStats(rights, rightType);
        final BigDecimal deficit = stats2.valueOfRights.subtract(stats1.valueOfRights);
        Assert.assertTrue(deficit.compareTo(BigDecimal.ZERO)>0);
        Assert.assertEquals(0, adjustBelowResults.deficit.compareTo(deficit));
        final FinalDiscountResults discount = adjustAboveRegional(rgValConfig, rights, rightType, deficit);
        logger.info("right type: [%s] reductions"
                    +" | initial: %d records %.2f rights (%.2f%s TVARV)"
                    +" | lowered: %d records %.2f rights"
                    +" | thresho: %d records %.2f rights"
                    +" | final value: %.2f%s"
                    +" | surplus of %.2f%s\n"
                    , rightType.name
                    , discount.allRecords
                    , discount.allRights
                    , discount.allRightsValue
                    , EUR
                    , discount.recordsLowered
                    , discount.rightsLowered
                    , discount.recordsLoweredThenRaised
                    , discount.rightsLoweredThenRaised
                    , discount.finalAllRightsValue
                    , EUR
                    , discount.surplus
                    , EUR
                    );
        
        final RightStats stats3 = Right.computeStats(rights, rightType);
        return discount.rights;
    }

    private static AdjustBelowResults adjustBelowRegional(final RegionalValues rgValConfig
                                                  , final List<Right> rights
                                                  , final RightType rightType) {


        int        allRecords               = 0;
        BigDecimal allRights                = BigDecimal.ZERO;
        BigDecimal allRightsValue           = BigDecimal.ZERO;
        int        recordsRaised            = 0;
        BigDecimal rightsRaised             = BigDecimal.ZERO;
        int        recordsFurtherRaised     = 0;
        BigDecimal rightsFurtherRaised      = BigDecimal.ZERO;
        BigDecimal finalAllRightsValue      = BigDecimal.ZERO;
        BigDecimal shortFall                = BigDecimal.ZERO;

        for (final Right right: rights) {
            if (right.type.equals(rightType)) {
                allRecords++;
                allRights = allRights.add(right.quantity);
                allRightsValue = allRightsValue.add(right.totalValue());
                final BigDecimal applicableRegionalValue = rgValConfig.valueFor(right.type);
                if (right.unit_value.compareTo(applicableRegionalValue)<0) {
                    recordsRaised++;
                    rightsRaised = rightsRaised.add(right.quantity);
                    final BigDecimal initialUV = right.unit_value.add(BigDecimal.ZERO);
                    final BigDecimal distance = applicableRegionalValue.subtract(initialUV);
                    Assert.assertTrue(distance.compareTo(BigDecimal.ZERO)>0);
                    final BigDecimal third = distance.divide(new BigDecimal(3), RoundingMode.HALF_UP);
                    BigDecimal new_unit_value = initialUV.add(third);
                    final BigDecimal sixtypc = applicableRegionalValue.multiply(new BigDecimal(0.6));
                    if (new_unit_value.compareTo(sixtypc)<0) {
                        recordsFurtherRaised++;
                        rightsFurtherRaised = rightsFurtherRaised.add(right.quantity);
                        new_unit_value = sixtypc;
                    }
                    new_unit_value = new_unit_value.setScale(3, RoundingMode.HALF_UP);
                    final BigDecimal prevValue= right.totalValue();
                    right.unit_value = new_unit_value;
                    final BigDecimal newValue = right.totalValue();
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
                final BigDecimal newValue = right.totalValue();
                finalAllRightsValue = finalAllRightsValue.add(newValue);
            }
        }
        return new AdjustBelowResults(allRecords,
                                      allRights,
                                      allRightsValue,
                                      recordsRaised,
                                      rightsRaised,
                                      recordsFurtherRaised,
                                      rightsFurtherRaised,
                                      finalAllRightsValue,
                                      shortFall);
    }

    private static FinalDiscountResults adjustAboveRegional(final RegionalValues rgValConfig
                                                            , final List<Right> rights
                                                            , final RightType rightType
                                                            , final BigDecimal shortFall) {
        final BigDecimal totalAbove = Right.computeStats(rights
                                                         , rightType
                                                         , RightValueSelector.ABOVE
                                                         , rgValConfig.valueFor(rightType)).valueOfRights;
        final BigDecimal minimumHorizontalDiscount = calcMinimumHorizontalDiscount(totalAbove, shortFall);
        logger.debug("minimum horizontal discount calculated as: %.5f\n", minimumHorizontalDiscount);
        BigDecimal prevSurplus = null;
        List<Right> rightsTentative = null;
        TentativeDiscountResults tentative = null;
        int rounds = 0;
        for (BigDecimal discount = minimumHorizontalDiscount;
             discount.compareTo(maxDiscount) <= 0;
             discount = discount.add(new BigDecimal(Math.pow(10, -DISCOUNT_SCALE)))) {
            rounds ++;
            if (rounds > 1)
                prevSurplus = tentative.surplus;
            rightsTentative = Right.copy(rights);
            tentative = tentativelyApplyDiscount(rightsTentative, rgValConfig, rightType, discount);
            final BigDecimal diff = shortFall.subtract(tentative.surplus);
            logger.trace("Applying a discount of %.5f I was able to recover %.5f out of %.5f (missing %.5f)\n"
                         , discount
                         , tentative.surplus
                         , shortFall
                         , diff);

            if (diff.compareTo(BigDecimal.ZERO)<=0)
                break;
        }
        Assert.assertNotNull(tentative);
        return new FinalDiscountResults(rounds, rightsTentative, tentative, prevSurplus);
    }

    private static TentativeDiscountResults tentativelyApplyDiscount(  final List<Right> rights
                                                                     , final RegionalValues rgValConfig
                                                                     , final RightType rightType
                                                                       , final BigDecimal discount) {
        int        allRecords                  = 0;
        BigDecimal allRights                   = BigDecimal.ZERO;
        BigDecimal allRightsValue              = BigDecimal.ZERO;
        int        recordsLowered              = 0;
        BigDecimal rightsLowered               = BigDecimal.ZERO;
        int        recordsLoweredThenRaised    = 0;
        BigDecimal rightsLoweredThenRaised     = BigDecimal.ZERO;
        BigDecimal finalAllRightsValue         = BigDecimal.ZERO;
        BigDecimal surplus                     = BigDecimal.ZERO;
        for (final Right r: rights) {
            if (r.type.equals(rightType)) {
                allRecords++;
                allRights = allRights.add(r.quantity);
                final BigDecimal prevValue = r.totalValue();
                allRightsValue = allRightsValue.add(prevValue);
                if (r.unit_value.compareTo(rgValConfig.valueFor(rightType))>0) {
                    recordsLowered++;
                    rightsLowered = rightsLowered.add(r.quantity);
                    BigDecimal newUnitValue = Util.mulWithScale(r.unit_value
                                                                , BigDecimal.ONE.subtract(discount)
                                                                , 3
                                                                , RoundingMode.HALF_UP);
                    if (newUnitValue.compareTo(rgValConfig.valueFor(rightType))<0) {
                        recordsLoweredThenRaised++;
                        rightsLoweredThenRaised = rightsLoweredThenRaised.add(r.quantity);
                        newUnitValue = rgValConfig.valueFor(rightType);
                    }
                    r.unit_value = newUnitValue;
                    final BigDecimal newValue = r.totalValue();
                    final BigDecimal diff = prevValue.subtract(newValue);
                    Assert.assertTrue(diff.compareTo(BigDecimal.ZERO)>=0);
                    surplus = surplus.add(diff);
                }
                final BigDecimal newValue = r.totalValue();
                finalAllRightsValue = finalAllRightsValue.add(newValue);
            }
        }
        return new TentativeDiscountResults(allRecords
                                            , allRights
                                            , allRightsValue
                                            , recordsLowered
                                            , rightsLowered
                                            , recordsLoweredThenRaised
                                            , rightsLoweredThenRaised
                                            , finalAllRightsValue
                                            , surplus);
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


