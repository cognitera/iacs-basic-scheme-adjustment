package gr.cognitera.util.rest;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import org.junit.Assert;

public class ValueOrInternalServerExceptionData<T> {


    public final T                       t;
    public final InternalServerExceptionData err;


    public ValueOrInternalServerExceptionData(final T t, final InternalServerExceptionData err) {
        Assert.assertFalse((t!=null) && (err!=null));
        this.t       = t;
        this.err = err;
    }

    public static <T> ValueOrInternalServerExceptionData<T> ok(T t) {
        return new ValueOrInternalServerExceptionData<T>(t, null);
    }

    public static ValueOrInternalServerExceptionData<Void> err(String message, String strServerTrace) {
        return new ValueOrInternalServerExceptionData<Void>(null,
                                                        new InternalServerExceptionData(message, strServerTrace));
    }


    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("t"  , t)
            .add("err", err)
            ;
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }    
}

