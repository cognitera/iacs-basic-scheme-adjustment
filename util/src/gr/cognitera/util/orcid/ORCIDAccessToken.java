package gr.cognitera.util.orcid;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;


public class ORCIDAccessToken {

    public String access_token;
    public String token_type;
    public String refresh_token;
    public String expires_in;
    public String scope;
    public String orcid;

    public ORCIDAccessToken() {}


    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("access_token", access_token)
            .add("token_type", token_type)
            .add("refresh_token", refresh_token)
            .add("expires_in", expires_in)
            .add("scope", scope)
            .add("orcid", orcid)
            ;
    }
}
