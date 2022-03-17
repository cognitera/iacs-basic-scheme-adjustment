package gr.cognitera.iacs.basic_scheme_adjustment;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import java.math.BigDecimal;

class AdjustBelowResults {
    public int        allRecords;
    public BigDecimal allRights;
    public BigDecimal allRightsValue;
    public int        recordsRaised;
    public BigDecimal rightsRaised;
    public int        recordsFurtherRaised;
    public BigDecimal rightsFurtherRaised;
    public BigDecimal finalAllRightsValue;
    public BigDecimal deficit;

    public AdjustBelowResults(final int        allRecords,
                              final BigDecimal allRights,
                              final BigDecimal allRightsValue,
                              final int        recordsRaised,
                              final BigDecimal rightsRaised,
                              final int        recordsFurtherRaised,
                              final BigDecimal rightsFurtherRaised,
                              final BigDecimal finalAllRightsValue,
                              final BigDecimal deficit) {
        this.allRecords = allRecords;
        this.allRights = allRights;
        this.allRightsValue = allRightsValue;
        this.recordsRaised = recordsRaised;
        this.rightsRaised = rightsRaised;
        this.recordsFurtherRaised = recordsFurtherRaised;
        this.rightsFurtherRaised = rightsFurtherRaised;
        this.finalAllRightsValue = finalAllRightsValue;
        this.deficit = deficit;
    }
    
    public String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("allRecords", allRecords)
            .add("allRights", allRights)
            .add("allRightsValue", allRightsValue)
            .add("recordsRaised", recordsRaised)
            .add("rightsRaised", rightsRaised)
            .add("recordsFurtherRaised", recordsFurtherRaised)
            .add("rightsFurtherRaised", rightsFurtherRaised)
            .add("finalAllRightsValue", finalAllRightsValue)
            .add("deficit", deficit)
            ;
    }
    
}
