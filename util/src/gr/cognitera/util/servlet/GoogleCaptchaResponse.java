package gr.cognitera.util.servlet;

import java.util.List;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import com.google.gson.annotations.SerializedName;

public class GoogleCaptchaResponse {

    @SerializedName("success")
    public boolean success;

    @SerializedName("challenge_ts")
    public String challengeTs;

    @SerializedName("hostname")
    public String hostname;

    @SerializedName("error-codes")
    public List<String> errorCodes;

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("success"        , success    )
            .add("challengeTs"    , challengeTs)
            .add("hostname"       , hostname   )
            .add("errorCodes"     , errorCodes )
            ;
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }
}
