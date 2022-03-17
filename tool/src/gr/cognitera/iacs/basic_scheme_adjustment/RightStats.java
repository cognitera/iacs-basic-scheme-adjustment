package gr.cognitera.iacs.basic_scheme_adjustment;

import java.math.BigDecimal;

public class RightStats {

    public int numOfRecords;
    public BigDecimal numOfRights;
    public BigDecimal valueOfRights;
    public BigDecimal lowestUnitValue;
    public BigDecimal highestUnitValue;


    public RightStats(final int numOfRecords,
                      final BigDecimal numOfRights,
                      final BigDecimal valueOfRights,
                      final BigDecimal lowestUnitValue,
                      final BigDecimal highestUnitValue) {

        this.numOfRecords = numOfRecords;
        this.numOfRights = numOfRights;
        this.valueOfRights = valueOfRights;
        this.lowestUnitValue = lowestUnitValue;
        this.highestUnitValue = highestUnitValue;
    }
}
