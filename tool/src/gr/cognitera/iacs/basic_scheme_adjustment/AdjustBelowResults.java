package gr.cognitera.iacs.basic_scheme_adjustment;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import java.math.BigDecimal;

class AdjustBelowResults {
    public BigDecimal initial;
    public int rightsRaised;
    public int rightsFurtherRaised;
    public BigDecimal finalV;
    public BigDecimal shortFall;

    public AdjustBelowResults(final BigDecimal initial,
                              final int rightsRaised,
                              final int rightsFurtherRaised,
                              final BigDecimal finalV,
                              final BigDecimal shortFall) {
        this.initial = initial;
        this.rightsRaised = rightsRaised;
        this.rightsFurtherRaised = rightsFurtherRaised;
        this.finalV = finalV;
        this.shortFall = shortFall;
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("initial", initial)
            .add("rightsRaised", rightsRaised)
            .add("rightsFurtherRaised", rightsFurtherRaised)
            .add("final", finalV)
            .add("shortFall", shortFall)
            ;
    }
    
}