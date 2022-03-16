package gr.cognitera.iacs.basic_scheme_adjustment;

import java.math.BigDecimal;


import org.junit.Assert;

import gr.cognitera.util.base.SCAUtils;

public class RegionalValues {

    public BigDecimal pasture; 
    public BigDecimal arable;
    public BigDecimal permaCrop;

    public BigDecimal valueFor(final RightType rt) {
        switch (rt) {
        case PASTURE:
            return this.pasture;
        case ARABLE:
            return this.arable;
        case PERMACROP:
            return this.permaCrop;
        default:
            Assert.fail();
            return SCAUtils.CANT_REACH_THIS_LINE(BigDecimal.class);
        }
    }


}
