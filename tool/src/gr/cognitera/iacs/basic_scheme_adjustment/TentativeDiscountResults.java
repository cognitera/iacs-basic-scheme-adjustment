package gr.cognitera.iacs.basic_scheme_adjustment;

import java.util.List;
import java.math.BigDecimal;

public class TentativeDiscountResults {
    public List<Right> rights;
    public BigDecimal shortFallRecovered;

    public TentativeDiscountResults(final List<Right> rights,
                                    final BigDecimal shortFallRecovered) {
        this.rights = rights;
        this.shortFallRecovered = shortFallRecovered;
    }
        
}
