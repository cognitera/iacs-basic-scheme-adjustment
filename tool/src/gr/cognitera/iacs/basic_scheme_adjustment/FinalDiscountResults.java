package gr.cognitera.iacs.basic_scheme_adjustment;

import java.util.List;
import java.math.BigDecimal;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;


public class FinalDiscountResults {
    public int rounds;
    public List<Right> rights;
    public BigDecimal shortFallRecovered;
    public BigDecimal prevShortFallRecovered;

    public FinalDiscountResults(final int rounds
                                , final List<Right> rights
                                , final BigDecimal shortFallRecovered
                                , final BigDecimal prevShortFallRecovered) {
        this.rounds = rounds;
        this.rights = rights;
        this.shortFallRecovered = shortFallRecovered;
        this.prevShortFallRecovered = prevShortFallRecovered;
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("rounds", rounds)
            .add("#rights", rights.size())
            .add("shortFallRecovered", shortFallRecovered)
            .add("prevShortFallRecovered", prevShortFallRecovered)
            ;
    }

    public BigDecimal surplus(final BigDecimal shortFall) {
        return this.shortFallRecovered.subtract(shortFall);
    }

    public BigDecimal previousSurplus(final BigDecimal shortFall) {
        return this.prevShortFallRecovered.subtract(shortFall);
    }
}
