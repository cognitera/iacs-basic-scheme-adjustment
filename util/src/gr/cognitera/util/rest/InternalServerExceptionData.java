package gr.cognitera.util.rest;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public class InternalServerExceptionData {

    public final String message;
    public final String strServerTrace;
    
    public InternalServerExceptionData(final String message, final String strServerTrace) {
        this.message        = message;
        this.strServerTrace = strServerTrace;
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("message"       , message)
            .add("strServerTrace", strServerTrace)
            ;
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }
}

