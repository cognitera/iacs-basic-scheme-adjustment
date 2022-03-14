package gr.cognitera.util.adql;

// imports needed by the test machinery

import org.junit.Assert;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Objects;

import org.junit.rules.ErrorCollector;
import org.junit.Rule;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;


// imports needed by the particular test logic

import gr.cognitera.util.base.Maybe;
import com.google.common.collect.Range;
import com.google.common.collect.BoundType;

class ConstructorSupplier {
    private final double ra;
    private final double sr;
    private final Maybe<ContiguousRAConstraint> expected;

    public ConstructorSupplier(final double ra,
                               final double sr,
                               final Maybe<ContiguousRAConstraint> expected) {
        this.ra       = ra;
        this.sr       = sr;
        this.expected = expected;
    }

    public Object[] supplyArguments() {
        return new Object[]{ra, sr, expected};
    }
}


@RunWith(Parameterized.class)
public class BoundingRegionCalculatorRaConstraintParametrizedTest {

    private final double ra;
    private final double sr;
    private final Maybe<ContiguousRAConstraint> expected;


    public BoundingRegionCalculatorRaConstraintParametrizedTest(final double ra,
                                                                final double sr,
                                                                final Maybe<ContiguousRAConstraint> expected) {
        this.ra       = ra;
        this.sr       = sr;
        this.expected = expected;
    }

    private static List<ConstructorSupplier> _data() {
        List<ConstructorSupplier> rv = new ArrayList<>();
        rv.add(new ConstructorSupplier(180, 0, Maybe.of(ContiguousRAConstraint.singleBand(Range.range((Double)180.0
                                                                                                      , BoundType.CLOSED
                                                                                                      , (Double)180.0
                                                                                                      , BoundType.CLOSED)))));
        rv.add(new ConstructorSupplier(180, 10, Maybe.of(ContiguousRAConstraint.singleBand(Range.range((Double)170.0
                                                                                                      , BoundType.CLOSED
                                                                                                      , (Double)190.0
                                                                                                      , BoundType.CLOSED)))));
        rv.add(new ConstructorSupplier(0  , 180, Maybe.of( (ContiguousRAConstraint) null)));
        rv.add(new ConstructorSupplier(180, 180, Maybe.of( (ContiguousRAConstraint) null)));
        rv.add(new ConstructorSupplier(360, 0, Maybe.of(ContiguousRAConstraint.singleBand(Range.range((Double)360.0
                                                                                                      , BoundType.CLOSED
                                                                                                      , (Double)360.0

                                                                                                      , BoundType.CLOSED)))));
        // test cases where RA-SR falls below the range [0...360]
        for (double ra = 0; ra <= 179; ra++) {
            for (double sr = ra+1; sr < 180; sr++) {
                rv.add(new ConstructorSupplier(ra, sr, Maybe.of(ContiguousRAConstraint.dualBand(Range.range((Double)360.0 + ra - sr
                                                                                                           , BoundType.CLOSED
                                                                                                           , (Double)360.0
                                                                                                           , BoundType.CLOSED),
                                                                                               Range.range((Double)0.0
                                                                                                           , BoundType.CLOSED
                                                                                                           , (Double) ra + sr
                                                                                                           , BoundType.CLOSED)))));
            }
        }

        // test cases where RA+SR falls above the range [0...360]
        for (double ra = 181; ra <= 360; ra++) {
            for (double sr = 360-ra+1; sr < 180; sr++) {
                rv.add(new ConstructorSupplier(ra, sr, Maybe.of(ContiguousRAConstraint.dualBand(Range.range((Double) ra - sr
                                                                                                             , BoundType.CLOSED
                                                                                                             , (Double)360.0
                                                                                                             , BoundType.CLOSED),
                                                                                                 Range.range((Double)0.0
                                                                                                             , BoundType.CLOSED
                                                                                                             , (Double) ra+sr-360
                                                                                                             , BoundType.CLOSED)))));
            }
        }


        
        return rv;
    }

    @Parameters(name = "{index}: RA={0} SR={1}}")
    public static Collection<Object[]> data() {
        List<Object[]> rv = new ArrayList<>();        
        for (ConstructorSupplier x: _data())
            rv.add(x.supplyArguments());
        return rv;
    }
    
    @Test
    public void asExpected() {
        final Maybe<ContiguousRAConstraint> actual = BoundingRegionCalculator.raConstraint(ra, sr);
        Assert.assertEquals(expected, actual);
    }
}
