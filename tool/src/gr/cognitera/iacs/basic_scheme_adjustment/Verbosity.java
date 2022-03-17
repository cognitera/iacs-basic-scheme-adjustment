package gr.cognitera.iacs.basic_scheme_adjustment;

import org.junit.Assert;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import gr.cognitera.util.base.SCAUtils;

public enum Verbosity {

    SILENT("silent"), INFO("info"), DEBUG("debug"), TRACE("trace");

    private String code;

    private Verbosity(final String code) {
        this.code = code;
    }

    public static Verbosity fromCode(final String code) {
        for (Verbosity x: Verbosity.values())
            if (x.code.equals(code))
                return x;
        return null;
    }

    
    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("code", code)
            ;
    }
    // returns true if [this] verbosity is greater than or equal to [x]
    public boolean gte(final Verbosity x) {
        if (x.equals(Verbosity.SILENT))
            Assert.fail("bug - did not expect gte to be called with this value");
        switch (this) {
        case SILENT:
            return false;
        case INFO:
            if (x.equals(TRACE) || x.equals(DEBUG))
                return false;
            else
                return true;
        case DEBUG:
            if (x.equals(TRACE))
                return false;
            else
                return true;
        case TRACE:
            return true;
        default:
            Assert.fail();
            return SCAUtils.CANT_REACH_THIS_LINE(boolean.class);
        }
    }
    




    
}
