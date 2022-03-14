package gr.cognitera.util.rest;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public class InternalServerException extends Exception {

    public final String strServerTrace;
    
    public InternalServerException(final InternalServerExceptionData data) {
        super(data.message);
        this.strServerTrace = data.strServerTrace;
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("message"           , getMessage())
            .add("strServerTrace"    , strServerTrace)
            ;
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }
}
