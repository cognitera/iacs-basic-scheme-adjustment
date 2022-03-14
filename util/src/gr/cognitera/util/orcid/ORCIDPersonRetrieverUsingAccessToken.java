package gr.cognitera.util.orcid;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;

import org.apache.http.Header;

import org.junit.Assert;
import org.apache.http.message.BasicHeader;

import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
                        

import gr.cognitera.util.rest.SimpleRestClient;
import gr.cognitera.util.rest.JsonSimpleRestClient;
import gr.cognitera.util.rest.HttpStatusNotOKish;
import gr.cognitera.util.rest.NameValuePairHelper;

import gr.cognitera.util.base.Util;


/**
 * Obtains a researcher's personal information using the two-step (read-public) access token
 * workflow as described
 * <a href="https://github.com/ORCID/ORCID-Source/tree/master/orcid-api-web#generate-a-two-step-read-public-access-token">here</a>.
 * <p>
 * This is also known as the <i>"2-legged OAuth Authorization"</i> and is described
 * <a href="https://members.orcid.org/api/oauth/2legged-oauth">here</a>. The broader context is given
 * <a href="https://members.orcid.org/api/oauth">here</a>.
 * <p>
 * See also <a href="https://groups.google.com/d/msg/orcid-api-users/wdwA37kIuX4/01deekXcAQAJ">this discussion</a>
 * in the ORCID API Users Google group.
 *
 */
public class ORCIDPersonRetrieverUsingAccessToken implements IORCIDPersonRetriever {

    final static Logger _logger = Logger.getLogger(ORCIDPersonRetrieverUsingAccessToken.class);

    final Logger logger;
    final String host;
    final String clientId;
    final String clientSecret;

    /**
     * Create an instance of this class by delegating to the
     * {@link #ORCIDPersonRetrieverUsingAccessToken(Logger, ORCIDEnvironment, String, String) main constructor}
     * passing {@code Logger} as {@code null}
     *
     * @param orcidEnvironment the {@link ORCIDEnvironment} enumeration that captures which ORCID API host
     *        to use (sandbox, public or member)
     * @param clientId the <i>Client ID</i> as described <a href="https://orcid.org/developer-tools">here</a>
     * @param clientSecret the <i>Client secret</i> as described <a href="https://orcid.org/developer-tools">here</a>
     */
    public ORCIDPersonRetrieverUsingAccessToken(final ORCIDEnvironment orcidEnvironment,
                                                final String clientId,
                                                final String clientSecret) {
        this(_logger, orcidEnvironment, clientId, clientSecret);
    }

    /**
     * Main constructor
     *
     * @param logger the {@code Logger} to use
     * @param orcidEnvironment the {@link ORCIDEnvironment} enumeration that captures which ORCID API host
     *        to use (sandbox, public or member)
     * @param clientId the <i>Client ID</i> as described <a href="https://orcid.org/developer-tools">here</a>
     * @param clientSecret the <i>Client secret</i> as described <a href="https://orcid.org/developer-tools">here</a>
     *
     */
    public ORCIDPersonRetrieverUsingAccessToken(final Logger logger,
                                                final ORCIDEnvironment orcidEnvironment,
                                                final String clientId,
                                                final String clientSecret) {
        Assert.assertNotNull("logger may not be null - use the other constructor for default logger", logger);
        Assert.assertTrue(Util.noneIsNull(orcidEnvironment, clientId, clientSecret));
        this.logger       = logger;
        this.host         = orcidEnvironment.getHost();
        this.clientId     = clientId;
        this.clientSecret = clientSecret;
    }


    private ORCIDAccessToken getAccessToken() throws IOException, HttpStatusNotOKish {
        return JsonSimpleRestClient._post(String.format("https://%s/oauth/token", host)
                                          , NameValuePairHelper.create("client_id"    , clientId,
                                                                       "client_secret", clientSecret,
                                                                       "scope"        , "/read-public",
                                                                       "grant_type"   , "client_credentials")
                                          , ORCIDAccessToken.class
                                          , false
                                          , logger
                                          // for some reason using "application/vnd.orcid+json"  here fails miserably with HTTP 500 (Internal Server Error)                                          
                                          , new BasicHeader("Accept", "application/json")); 
                                          
    }

    private String getPersonJsonRawWithAccessToken(final String accessToken, final String orcid) throws IOException, HttpStatusNotOKish {
        return SimpleRestClient._get(String.format("https://%s/v2.1/%s/person", host, orcid)
                                     , logger
                                     , ORCIDUtil.orcidAcceptHeader()
                                     , new BasicHeader("Authorization", String.format("Bearer %s", accessToken)));
    }

    @Override
    public ORCIDPerson getPerson(final String orcid) throws IOException, HttpStatusNotOKish, CannotConstructORCIDPersonFromJson {
        /* KISS: get a new bearer token every blessed time - don't bother with
           reusing the previous one or using the refresh token field
        */
        final ORCIDAccessToken accessToken = getAccessToken();
        final String json = getPersonJsonRawWithAccessToken(accessToken.access_token, orcid);
        logger.trace(String.format("ORCID record fetched in JSON format as follows:\n%s\n", json));
        return ORCIDPerson.fromJson(json);
    }
}
