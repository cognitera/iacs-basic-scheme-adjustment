package gr.cognitera.util.orcid;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import com.google.gson.annotations.SerializedName;

/**
 * Holds a typical error message returned (in JSON format) from the ORCID servers.
 * Some examples of what I've encountered:
 *
 *  
 * Ex.1. Error message when the ORCID does not exist:
 *  
 *    {
 *      "response-code" : 404,
 *      "developer-message" : "404 Not Found: The resource was not found. Full validation error: ORCID iD 0000-0000-0000-0001 not found",
 *      "user-message" : "The resource was not found.",
 *      "error-code" : 9016,
 *      "more-info" : "https://members.orcid.org/api/resources/troubleshooting"
 *    }
 *
 *
 * Ex.2. Error message when there's something wrong in the REST path:
 *
 *    {
 *      "response-code" : 404,
 *      "developer-message" : "404 Not Found: The resource was not found. Full validation error: null for uri: http://pub.orcid.org/orcid-pub-web/v2.1/0000-0003-3031-1435/personXXX",
 *      "user-message" : "The resource was not found.",
 *      "error-code" : 9016,
 *      "more-info" : "https://members.orcid.org/api/resources/troubleshooting"
 *    }
 *
 *
 */
public class ORCIDError {

    @SerializedName("response-code")
    public int responseCode;

    @SerializedName("developer-message")
    public String developerMessage;

    @SerializedName("user-message")
    public String userMessage;

    @SerializedName("error-code")
    public int errorCode;

    @SerializedName("more-info")
    public String moreInfo;

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("responseCode"        , responseCode)
            .add("developerMessage", developerMessage)
            .add("userMessage", userMessage)
            .add("errorCode", errorCode)
            .add("moreInfo", moreInfo)
            ;
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    

}
