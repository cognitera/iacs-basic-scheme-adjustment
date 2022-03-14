package gr.cognitera.util.orcid;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;


public class CannotConstructORCIDPersonFromJson extends Exception {

    public final String json;
    public final String errMsg;

    public CannotConstructORCIDPersonFromJson(final String json
                                              , final String errMsg) {
        this.json   = json;
        this.errMsg = errMsg;
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("json"   , json)
            .add("errMsg" , errMsg)
            ;
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }
    
}
