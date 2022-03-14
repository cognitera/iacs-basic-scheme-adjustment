package gr.cognitera.util.orcid;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;

import org.apache.http.Header;

import org.junit.Assert;
import org.apache.http.message.BasicHeader;

import org.apache.log4j.Logger;

import gr.cognitera.util.rest.SimpleRestClient;
import gr.cognitera.util.rest.HttpStatusNotOKish;


/**
 * Obtains a researcher's personal information using annonymous access.
 * <p>
 * See <a href="https://groups.google.com/d/msg/orcid-api-users/wdwA37kIuX4/01deekXcAQAJ">this discussion</a>
 * in the ORCID API Users Google group.
 *
 */
public class ORCIDPersonRetrieverUsingAnonymousAccess implements IORCIDPersonRetriever {

    final static Logger _logger = Logger.getLogger(ORCIDPersonRetrieverUsingAnonymousAccess.class);

    final Logger logger;
    final String host;

    /**
     * Create an instance of this class by delegating to the
     * {@link #ORCIDPersonRetrieverUsingAnonymousAccess(Logger, ORCIDEnvironment) main constructor}
     * passing {@code Logger} as {@code null}
     *
     * @param orcidEnvironment the {@link ORCIDEnvironment} enumeration that captures which ORCID API host
     *        to use (sandbox, public or member)
     *
     */    
    public ORCIDPersonRetrieverUsingAnonymousAccess(final ORCIDEnvironment orcidEnvironment) {
        this(_logger, orcidEnvironment);
    }

    /**
     * Main constructor
     *
     * @param logger the {@code Logger} to use
     * @param orcidEnvironment the {@link ORCIDEnvironment} enumeration that captures which ORCID API host
     *        to use (sandbox, public or member)
     *
     */
    public ORCIDPersonRetrieverUsingAnonymousAccess(final Logger logger, final ORCIDEnvironment orcidEnvironment) {
        Assert.assertNotNull("logger may not be null - use the other constructor for default logger", logger);
        this.logger = logger;
        this.host   = orcidEnvironment.getHost();
    }

    private static String getAsJson(final Logger logger, final String target) throws IOException, HttpStatusNotOKish {
        return SimpleRestClient._get(target,
                                     logger,
                                     ORCIDUtil.orcidAcceptHeader());
    }

    @Override
    public ORCIDPerson getPerson(final String orcid) throws IOException, HttpStatusNotOKish, CannotConstructORCIDPersonFromJson {
        final String URL = String.format("https://%s/v2.1/%s/person", host, orcid);
        logger.debug(String.format("getPerson(%s) :: About to fetch ORCID record with a GET call to: %s\n"
                                   , orcid
                                   , URL));
        final String json = getAsJson(logger, URL);
        logger.trace(String.format("getPerson(%s) :: ORCID record fetched in JSON format as follows:\n%s\n"
                                   , orcid
                                   , json));
        return ORCIDPerson.fromJson(json);
    }
}
