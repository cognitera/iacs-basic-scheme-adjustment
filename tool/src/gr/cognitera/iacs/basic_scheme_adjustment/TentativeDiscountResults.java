package gr.cognitera.iacs.basic_scheme_adjustment;

import java.util.List;
import java.math.BigDecimal;

public class TentativeDiscountResults {

    public int        allRecords;
    public BigDecimal allRights;
    public BigDecimal allRightsValue;
    public int        recordsLowered;
    public BigDecimal rightsLowered;
    public int        recordsLoweredThenRaised;
    public BigDecimal rightsLoweredThenRaised;
    public BigDecimal finalAllRightsValue;
    public BigDecimal surplus;

    public TentativeDiscountResults(final int        allRecords,
                                    final BigDecimal allRights,
                                    final BigDecimal allRightsValue,
                                    final int        recordsLowered,
                                    final BigDecimal rightsLowered,
                                    final int        recordsLoweredThenRaised,
                                    final BigDecimal rightsLoweredThenRaised,
                                    final BigDecimal finalAllRightsValue,
                                    final BigDecimal surplus) {
        this.allRecords = allRecords;
        this.allRights = allRights;
        this.allRightsValue = allRightsValue;
        this.recordsLowered = recordsLowered;
        this.rightsLowered = rightsLowered;
        this.recordsLoweredThenRaised = recordsLoweredThenRaised;
        this.rightsLoweredThenRaised = rightsLoweredThenRaised;
        this.finalAllRightsValue = finalAllRightsValue;
        this.surplus = surplus;
    }
}
