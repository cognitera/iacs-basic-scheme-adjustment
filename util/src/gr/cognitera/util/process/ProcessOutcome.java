package gr.cognitera.util.process;

import java.util.List;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;


public final class ProcessOutcome {

    public final String output;
    public final String error;
    public final int    exitCode;

    public ProcessOutcome(final   String output
                          , final String error
                          , final int    exitCode) {
        this.output    = output;
        this.error     = error;
        this.exitCode  = exitCode;
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("output"   , output)
            .add("error"    , error)
            .add("exitCode" , exitCode)
            ;
    }


    @Override
    public String toString() {
        return toStringHelper().toString();
    }
}
