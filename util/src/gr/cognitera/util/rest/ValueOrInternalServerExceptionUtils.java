package gr.cognitera.util.rest;

import org.junit.Assert;

public final class ValueOrInternalServerExceptionUtils {

    private ValueOrInternalServerExceptionUtils() {}

    public static <T> T returnValue(ValueOrInternalServerExceptionData<T> rv) throws InternalServerException {
        Assert.assertNotNull(rv);        
        if (rv.err==null)
            return rv.t;
        else
            throw new InternalServerException(rv.err);
    }

    public static void doNothingUnlessException(ValueOrInternalServerExceptionData<Void> rv) throws InternalServerException {
        if (rv.err==null)
            ;
        else
            throw new InternalServerException(rv.err);
    }



}
