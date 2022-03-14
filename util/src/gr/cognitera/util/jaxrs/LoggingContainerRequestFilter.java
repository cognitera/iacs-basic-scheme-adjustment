package gr.cognitera.util.jaxrs;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import javax.ws.rs.container.ResourceInfo;
    


import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;

/*
 * When we migrated from JBoss EAP 6.2 to EAP 7.1 (JAX-RS 1.1 to 2.2 respectively) in June 2019
 * I established that the @Provider annotation is not necessary. Either of the below two methods
 * is sufficient to bring the filter into action:
 * 
 * (a) use a restesy.providers context parameter in the web.xml of the application referencing this class
 * (b) instatiate yourself the class inside the JaxRsApplication constructor and place it in the Singletons
 *     set.
 * 
 * Method (b) has the additional benefit that you can supply parameters in the constructor.
 *
 *
 */
// @javax.ws.rs.ext.Provider
public final class LoggingContainerRequestFilter implements ContainerRequestFilter {

    private final Logger logger;

    public LoggingContainerRequestFilter(final Logger logger) {
        this.logger = logger;
        logger.debug(String.format("[inside class %s]: filter created"
                                   , this.getClass().getName()));
    }

    @Context
    private ResourceInfo resourceInfo;


    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        final Class<?> klass = resourceInfo.getResourceClass();
        final Method method =  resourceInfo.getResourceMethod();
        logger.debug(String.format("[inside class %s] HTTP verb is: [%s] - attempting to invoke method [%s] on class [%s]"
                                   , this.getClass().getName()
                                   , requestContext.getMethod()
                                   , method.getName()
                                   , klass.getName()));
    }
}
