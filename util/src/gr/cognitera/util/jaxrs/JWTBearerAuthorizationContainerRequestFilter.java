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
import javax.crypto.SecretKey;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;

import org.junit.Assert;
import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.ExpiredJwtException;


import gr.cognitera.util.jwt.JWTUtil;

public abstract class JWTBearerAuthorizationContainerRequestFilter
    extends AbstractBearerAuthorizationContainerRequestFilter
    implements ContainerRequestFilter {

    public JWTBearerAuthorizationContainerRequestFilter(final List<? extends Class<?>> guardedClasses,
                                                     final Logger logger) {
        super(guardedClasses, logger);
    }


    protected abstract SecretKey getSecretKey();
    protected abstract void processClaims(final ContainerRequestContext requestContext, final Claims claims);
    protected abstract String jwtTokenExpired(final String token);
    protected abstract String jwtTokenNotValid(final String token);
    
    protected void bearerSpecificFilter(final ContainerRequestContext requestContext, final String jwtToken) {

        try {
            final Claims claims = JWTUtil.verifyJWS(getSecretKey(), jwtToken);
            processClaims(requestContext, claims);
        } catch (ExpiredJwtException e) {
            logger.debug(String.format("token [%s] is apparently expired:\n%s",
                                       jwtToken,
                                       Throwables.getStackTraceAsString(e)));
            abortUnauthorizedRequest(requestContext, jwtTokenExpired(jwtToken));
        } catch (JwtException e) {
            logger.debug(String.format("while validating JWT [%s], I encountered some other [%s] exception:\n%s",
                                       jwtToken,
                                       JwtException.class,
                                       Throwables.getStackTraceAsString(e)));
            abortUnauthorizedRequest(requestContext, jwtTokenNotValid(jwtToken));
        }
    }


}
