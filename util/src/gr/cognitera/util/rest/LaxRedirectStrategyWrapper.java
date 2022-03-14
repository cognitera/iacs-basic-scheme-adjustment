package gr.cognitera.util.rest;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ProtocolException;

import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;

import org.apache.http.client.methods.HttpRequestWrapper;

import org.apache.log4j.Logger;

public final class LaxRedirectStrategyWrapper implements RedirectStrategy {

    private final Logger logger;
    private final LaxRedirectStrategy strategy;

    public LaxRedirectStrategyWrapper(final Logger logger) {
        this.logger = logger;
        this.strategy = new LaxRedirectStrategy();
    }

    @Override
    public HttpUriRequest getRedirect(final HttpRequest request
                                      , final HttpResponse response
                                      , final HttpContext context) throws ProtocolException {
        final HttpUriRequest rv= strategy.getRedirect(request, response, context);
        logger.debug(String.format("\n\n\n\n\n\n\n%s#getRedirect:"
                                   +"\nrequest class is: [%s]"
                                   +"\nrequest value is: [%s]"
                                   +"\nresponse class is: [%s]"
                                   +"\nresponse value is: [%s]"
                                   +"\nRV class is: [%s]"
                                   +"\nRV value is: [%s]"
                                   +"\n\n\n\n\n\n\n"
                                   , this.getClass().getName()
                                   , request.getClass().getName()
                                   , request
                                   , response.getClass().getName()                                   
                                   , response
                                   , rv.getClass().getName()
                                   , rv));

        HttpUriRequest rvModified = null;
        if (request.getRequestLine().getMethod().equals("POST") &&
            (response.getStatusLine().getStatusCode()==302) &&
            rv.getMethod().equals("GET")) {
            logger.debug("we've encountered a case where an HTTP POST was changed into HTTP GET by dint of a server 302 redirect");
            rvModified = replacePostWithSecurePost(request, rv);
        }

        final HttpUriRequest effectiveRv = (rvModified==null)?rv:rvModified;
        logger.debug(String.format("%s#getRedirect: returning: [%s] / class is: [%s]"
                                   , this.getClass().getName()
                                   , effectiveRv
                                   , effectiveRv.getClass().getName()));
        return effectiveRv;
    }

    private HttpUriRequest replacePostWithSecurePost(final HttpRequest request, final HttpRequest redirectedRequest) {
        final String originalURI = request          .getRequestLine().getUri();
        final String modifiedURI = redirectedRequest.getRequestLine().getUri();
        logger.debug(String.format("%s#replacePostWithSecurePost originalURI is: [%s], modifiedURI is: [%s]"
                                   , this.getClass().getName()
                                   , originalURI
                                   , modifiedURI));
        final HttpRequestWrapper rv = HttpRequestWrapper.wrap(request);
        try {
            rv.setURI(new URI(modifiedURI));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return rv;
    }

    @Override
    public boolean isRedirected(final HttpRequest request
                                , final HttpResponse response
                                , final HttpContext context) throws ProtocolException {
        final boolean rv = strategy.isRedirected(request, response, context);
        logger.debug(String.format("%s#isRedirected: request is: [%s], returning: %b"
                                   , this.getClass().getName()
                                   , request
                                   , rv));
        return rv;
    }
}

