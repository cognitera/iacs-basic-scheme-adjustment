package gr.cognitera.util.jaxrs;

import java.util.List;
import java.util.Collections;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.HttpHeaders;

import javax.ws.rs.container.ResourceInfo;

import org.junit.Assert;

import org.apache.log4j.Logger;


    


import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;


public abstract class AbstractAuthorizationContainerRequestFilter implements ContainerRequestFilter {

    private final List<? extends Class<?>> guardedClasses;
    protected final Logger logger;

    public AbstractAuthorizationContainerRequestFilter(final List<? extends Class<?>> guardedClasses,
                                                       final Logger logger) {
        Assert.assertNotNull(guardedClasses);
        Assert.assertNotNull(logger);
        this.guardedClasses = guardedClasses;
        this.logger = logger;
    }

    @Context
    private ResourceInfo resourceInfo;

    @Context
    private HttpServletRequest request;

    @SuppressWarnings("rawtypes")
    public final boolean protects(final Class c, final Method m) {
        // simplest possible implementation for the time being: require authorization for *all* methods
        // of any of the guarded class
        for (Class<?> guardedClass: guardedClasses)
            if (c.equals( guardedClass ))
                return true;
        return false;
    }

    protected abstract String noAuthorizationHeaderPresentPayload();
    protected abstract String moreThanOneAuthorizationHeaderPresentPayload(final int numOfAuthorizationHeaders);

    protected abstract void methodSpecificFilter(final ContainerRequestContext requestContext, final String authHeaderValue) throws IOException;
    

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        final Class<?> klass = resourceInfo.getResourceClass();
        final Method method =  resourceInfo.getResourceMethod();
        logger.debug(String.format("[inside class %s] HTTP verb is: [%s] - attempting to invoke method [%s] on class [%s]"
                                   , this.getClass().getName()
                                   , requestContext.getMethod()
                                   , method.getName()
                                   , klass.getName()));
        if (protects(klass, method)) {
            logger.debug(String.format("Authorization check is triggered for method [%s] on class [%s]"
                                       , method.getName()
                                       , klass.getName()));
            List<String> authorizationHeaders = Collections.list(request.getHeaders(HttpHeaders.AUTHORIZATION));
            if (authorizationHeaders==null) {
                abortUnauthorizedRequest(requestContext, noAuthorizationHeaderPresentPayload());
                return;
            } else if (authorizationHeaders.size() != 1) {
                abortUnauthorizedRequest(requestContext, moreThanOneAuthorizationHeaderPresentPayload(authorizationHeaders.size()));
                return;
            } else {
                Assert.assertEquals(1, authorizationHeaders.size());
                final String authHeaderValue = authorizationHeaders.get(0);
                methodSpecificFilter(requestContext, authHeaderValue);
            }
            logger.debug(String.format("Authorization check on method [%s] on class [%s] was successful"
                                       , method.getName()
                                       , klass.getName()));
        } else {
            logger.debug(String.format("Authorization check is not enabled for method [%s] on class [%s] - letting the"
                                       +" request proceed with reckless abandon"
                                       , method.getName()
                                       , klass.getName()));
        }
    }

    protected void abortUnauthorizedRequest(final ContainerRequestContext requestContext
                                            , final String msg) {
        logger.debug(String.format("aborting the request with status: [%s] and message: [%s]"
                                   , Response.Status.UNAUTHORIZED
                                   , msg));
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                                 .entity(msg)
                                 .build());
    }
}

