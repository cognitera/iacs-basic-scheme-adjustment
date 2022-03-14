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


public class BasicAuthorizationContainerRequestFilter implements ContainerRequestFilter {

    private final String user;
    private final String pwd;
    private final List<? extends Class<?>> guardedClasses;
    private final Logger logger;

    public BasicAuthorizationContainerRequestFilter(final String user,
                                                    final String pwd,
                                                    final List<? extends Class<?>> guardedClasses,
                                                    final Logger logger) {
        Assert.assertNotNull(user);
        Assert.assertNotNull(pwd);
        Assert.assertNotNull(guardedClasses);
        Assert.assertNotNull(logger);
        this.user = user;
        this.pwd  = pwd;
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
                final String msg = String.format("No %s headers were present"
                                                 , HttpHeaders.AUTHORIZATION);
                abortUnauthorizedRequest(requestContext, msg);
                return;
            } else if (authorizationHeaders.size() != 1) {
                final String msg = String.format("Expected exactly %d %s headers - received %d"
                                                 , 1
                                                 , HttpHeaders.AUTHORIZATION
                                                 , authorizationHeaders.size());
                abortUnauthorizedRequest(requestContext, msg);
                return;
            } else {
                Assert.assertEquals(1, authorizationHeaders.size());
                final String authString = authorizationHeaders.get(0);
                final String[] authParts = authString.split("\\s+");
                if (authParts.length!=2) {
                    final String msg = String.format("malformed %s header: [%s]"
                                                     , HttpHeaders.AUTHORIZATION
                                                     , authString);
                    abortUnauthorizedRequest(requestContext, msg);
                    return;
                } else {
                    Assert.assertEquals(2, authParts.length);            
                    final String authMethod = authParts[0];
                    if (!"Basic".equals(authMethod)) {
                        final String msg = String.format("unrecognized authentication method in %s header: [%s]"
                                                         , HttpHeaders.AUTHORIZATION
                                                         , authMethod);
                        abortUnauthorizedRequest(requestContext, msg);
                        return;
                    } else {
                        final String authInfo   = authParts[1];
                        final byte[] bytes = DatatypeConverter.parseBase64Binary(authInfo);
                        final String decodedAuth = new String(bytes, StandardCharsets.US_ASCII);
                        final String[] usernamePassword = decodedAuth.split(":");
                        if (usernamePassword.length!=2) {
                            final String msg = String.format("malformed username:password string in %s header; Base64: [%s], decoded: [%s]"
                                                             , HttpHeaders.AUTHORIZATION
                                                             , authInfo
                                                             , decodedAuth);
                            abortUnauthorizedRequest(requestContext, msg);
                            return;
                        } else {
                            Assert.assertEquals(2, usernamePassword.length);
                            final String username = usernamePassword[0];
                            final String password = usernamePassword[1];
                            if ((!this.user.equals(username)) || (!this.pwd.equals(password))) {
                                final String msg = String.format("unrecognized username/password pair in %s header: %s/%s"
                                                                 , HttpHeaders.AUTHORIZATION
                                                                 , username
                                                                 , password);
                                abortUnauthorizedRequest(requestContext, msg);
                                return;
                            }
                        }
                    }
                }
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

