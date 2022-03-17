package gr.cognitera.iacs.basic_scheme_adjustment;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public enum RightValueSelector {

    ALL("all"), ABOVE("above"), EXACT("exact"), BELOW("below");

    private String code;

    private RightValueSelector(final String code) {
        this.code = code;
    }

    
}

    
