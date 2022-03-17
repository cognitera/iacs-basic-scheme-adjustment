package gr.cognitera.iacs.basic_scheme_adjustment;

import java.util.List;
import java.math.BigDecimal;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;


public class FinalDiscountResults {
    public int rounds;
    public List<Right> rights;
    public int        allRecords;
    public BigDecimal allRights;
    public BigDecimal allRightsValue;
    public int        recordsLowered;
    public BigDecimal rightsLowered;
    public int        recordsLoweredThenRaised;
    public BigDecimal rightsLoweredThenRaised;
    public BigDecimal finalAllRightsValue;
    public BigDecimal discountUsed;
    public BigDecimal surplus;

    public BigDecimal prevSurplus;

    public FinalDiscountResults(final int rounds
                                , final List<Right> rights
                                , final TentativeDiscountResults results
                                , final BigDecimal discountUsed
                                , final BigDecimal prevSurplus) {
        this.rounds           = rounds;
        this.rights           = rights;
        
        this.allRecords               = results.allRecords;
        this.allRights                = results.allRights;
        this.allRightsValue           = results.allRightsValue;
        this.recordsLowered           = results.recordsLowered;
        this.rightsLowered            = results.rightsLowered;
        this.recordsLoweredThenRaised = results.recordsLoweredThenRaised;
        this.rightsLoweredThenRaised  = results.rightsLoweredThenRaised;
        this.finalAllRightsValue      = results.finalAllRightsValue;
        this.surplus                  = results.surplus;

        this.discountUsed             = discountUsed;
        this.prevSurplus              = prevSurplus;
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("rounds", rounds)
            .add("#rights", rights.size())
            .add("surplus", surplus)
            .add("prev-surplus", prevSurplus)
            ;
    }

    public BigDecimal savings(final BigDecimal shortFall) {
        return this.surplus.subtract(shortFall);
    }

    public BigDecimal prevSavings(final BigDecimal shortFall) {
        return this.prevSurplus.subtract(shortFall);
    }
}
