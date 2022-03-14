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


public abstract class AbstractBearerAuthorizationContainerRequestFilter
    extends AbstractAuthorizationContainerRequestFilter
    implements ContainerRequestFilter {

    public AbstractBearerAuthorizationContainerRequestFilter(final List<? extends Class<?>> guardedClasses,
                                                     final Logger logger) {
        super(guardedClasses, logger);
    }
    
    protected abstract String malformedAuthorizationHeaderPayload(final String authHeader, final int numOfAuthParts);
    protected abstract String unrecognizedAuthorizationMethod(final String authMethod);

    protected abstract void bearerSpecificFilter(final ContainerRequestContext requestContext, final String bearerValue);

    @Override
    protected final void methodSpecificFilter(final ContainerRequestContext requestContext, final String authHeaderValue) throws IOException {
        final String[] authParts = authHeaderValue.split("\\s+");
        if (authParts.length!=2) {
            abortUnauthorizedRequest(requestContext, malformedAuthorizationHeaderPayload(authHeaderValue, authParts.length));
            return;
        } else {
            Assert.assertEquals(2, authParts.length);            
            final String authMethod = authParts[0];
            if (!"Bearer".equals(authMethod)) {
                final String msg = String.format("unrecognized authentication method in %s header: [%s]"
                                                 , HttpHeaders.AUTHORIZATION
                                                 , authMethod);
                abortUnauthorizedRequest(requestContext, unrecognizedAuthorizationMethod(authMethod));
                return;
            } else {
                final String bearerValue   = authParts[1];
                bearerSpecificFilter(requestContext, bearerValue);
            }
        }
    }
}
