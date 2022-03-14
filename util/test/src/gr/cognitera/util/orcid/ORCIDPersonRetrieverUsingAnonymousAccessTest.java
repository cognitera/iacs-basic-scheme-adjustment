package gr.cognitera.util.orcid;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.everyItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.net.URL;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import org.junit.rules.ErrorCollector;
import org.junit.Rule;

import javax.xml.bind.DatatypeConverter;

import com.google.common.base.Joiner;
import com.google.common.io.Files;


public class ORCIDPersonRetrieverUsingAnonymousAccessTest {

    @Test
    public void demonstrateThatClassesAreVisible() {
        final ORCIDPersonRetrieverUsingAnonymousAccess x1 = null;
        final ORCIDPerson                              x2 = null;
    }

    final private IORCIDPersonRetriever orcidRetriever = new ORCIDPersonRetrieverUsingAnonymousAccess(ORCIDEnvironment.PRODUCTION_REGISTRY_PUBLIC_API);    

    //    @Test
    public void productionServerBug_2018_11_16_i() {
        
        /*
         *
         *    P R O V E N A N C E    ,    R E S O L U T I O N    a n d    F I X
         *
         * [1] Email from [Tara Gokas] to [cycledev]                                 on 2018-11-16_T_1030 subject "Re: CPS webpage error"
         * [2] Email from [Tara Gokas] to [cycledev]                                 on 2018-11-16_T_1042 subject "Re: [cycledev] CPS webpage error"
         * [3] Email from [Tara Gokas] to [Menelaos Perdikeas]                       on 2018-11-16_T_1501 subject "Re: [cycledev] Re: CPS webpage error"
         * 
         * 
         * The trace in the JBoss server log file was the following:
         *  ---%<----------------------------------------------------------------------------------------------------------------
         *   [16/Nov/2018:10:15:14,370 -0500] ERROR [org.apache.catalina.core.ContainerBase.[jboss.web].[default-host].[/cxcaccount].[mvc-dispatcher]] (http-/0.0.0.0:8080-18) JBWEB000236: Servlet.service() for servlet mvc-dispatcher threw exception: java.lang.ClassCastException: com.google.gson.JsonNull cannot be cast to com.google.gson.JsonObject
         *   at com.google.gson.JsonObject.getAsJsonObject(JsonObject.java:191) [gson-2.8.0.jar:]
         *   at gr.cognitera.util.orcid.ORCIDPerson.fromJson(ORCIDPerson.java:68) [jutil-1.0.0.jar:]
         *   at gr.cognitera.util.orcid.ORCIDPersonRetrieverUsingAnonymousAccess.getPerson(ORCIDPersonRetrieverUsingAnonymousAccess.java:71) [jutil-1.0.0.jar:]
         *   at edu.harvard.cda.cxcaccount.webgui.AccountInfoValidator.validateCoreView(AccountInfoValidator.java:127) [classes:]
         *   at edu.harvard.cda.cxcaccount.webgui.AccountInfoValidator.validate(AccountInfoValidator.java:66) [classes:]
         *   at org.springframework.validation.DataBinder.validate(DataBinder.java:894) [spring-context-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at org.springframework.web.method.annotation.ModelAttributeMethodProcessor.validateIfApplicable(ModelAttributeMethodProcessor.java:169) [spring-web-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at org.springframework.web.method.annotation.ModelAttributeMethodProcessor.resolveArgument(ModelAttributeMethodProcessor.java:116) [spring-web-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at org.springframework.web.method.support.HandlerMethodArgumentResolverComposite.resolveArgument(HandlerMethodArgumentResolverComposite.java:121) [spring-web-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at org.springframework.web.method.support.InvocableHandlerMethod.getMethodArgumentValues(InvocableHandlerMethod.java:160) [spring-web-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:129) [spring-web-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:116) [spring-webmvc-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:827) [spring-webmvc-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:738) [spring-webmvc-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:85) [spring-webmvc-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:963) [spring-webmvc-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:897) [spring-webmvc-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:970) [spring-webmvc-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:872) [spring-webmvc-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at javax.servlet.http.HttpServlet.service(HttpServlet.java:754) [jboss-servlet-api_3.0_spec-1.0.2.Final-redhat-1.jar:1.0.2.Final-redhat-1]
         *   at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:846) [spring-webmvc-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at javax.servlet.http.HttpServlet.service(HttpServlet.java:847) [jboss-servlet-api_3.0_spec-1.0.2.Final-redhat-1.jar:1.0.2.Final-redhat-1]
         *   at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:295) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:214) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at edu.harvard.cda.cxcaccount.webgui.LogMDCUsernameFilter.doFilter(LogMDCUsernameFilter.java:50) [classes:]
         *   at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:246) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:214) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at gr.cognitera.util.servlet.LogMDCClientIPFilter.doFilter(LogMDCClientIPFilter.java:40) [jutil-1.0.0.jar:]
         *   at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:246) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:214) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:197) [spring-web-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107) [spring-web-4.3.5.RELEASE.jar:4.3.5.RELEASE]
         *   at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:246) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:214) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:230) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:149) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at org.jboss.as.web.security.SecurityContextAssociationValve.invoke(SecurityContextAssociationValve.java:169) [jboss-as-web-7.3.0.Final-redhat-14.jar:7.3.0.Final-redhat-14]
         *   at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:145) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:97) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at org.apache.catalina.valves.AccessLogValve.invoke(AccessLogValve.java:559) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:102) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:336) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at org.apache.coyote.http11.Http11Processor.process(Http11Processor.java:856) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.process(Http11Protocol.java:653) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at org.apache.tomcat.util.net.JIoEndpoint$Worker.run(JIoEndpoint.java:920) [jbossweb-7.2.2.Final-redhat-1.jar:7.2.2.Final-redhat-1]
         *   at java.lang.Thread.run(Thread.java:748) [rt.jar:1.7.0_141]
         *  ---------------------------------------------------------------------------------------------------------------->%---
         */



        try {
            final String ORCID = "0000-0003-3847-3957";
            Assert.assertTrue(ORCIDValidator.isWellFormed(ORCID));
            final ORCIDPerson x = orcidRetriever.getPerson(ORCID);
            System.out.printf("person is: \n%s\n", x);
        } catch (Throwable t) {
            final Class TOLERATED_EXCEPTION_CLASS = CannotConstructORCIDPersonFromJson.class;            
            final Class exceptionClass = t.getClass();
            if (exceptionClass.equals(TOLERATED_EXCEPTION_CLASS))
                ; // do nothing, we tolerate this particular exception
            else
                throw new RuntimeException(t);
        }
    }


    @Test
    public void productionServerBug_2018_11_16_i_2() {
        
        /* 
         * This is related to [productionServerBug_2018_11_16_i]. I've simply isolated the offending JSON payload
         * so that even if the user updates his ORCID record we'll still have the payload that triggered this problem.
         * Moreover, in contrast to [productionServerBug_2018_11_16_i], this time we don't just "tolerate" this particular
         * exception, rather we demand that it occurs.
         */
        final Class ANTICIPATED_EXCEPTION_CLASS = CannotConstructORCIDPersonFromJson.class;
        boolean anticipatedExceptionWasThrown = false;
        try {
            final String json = Files.toString(new File("test/test-files/orcid/0000-0003-3847-3957.json"), StandardCharsets.UTF_8);
            final ORCIDPerson x = ORCIDPerson.fromJson(json);
            System.out.printf("person is: \n%s\n", x);
        } catch (Throwable t) {
            final Class exceptionClass = t.getClass();
            if (exceptionClass.equals(ANTICIPATED_EXCEPTION_CLASS)) {
                System.out.printf("encountered anticipated exception: [%s]; all's well!"
                                  , exceptionClass.getSimpleName());
                anticipatedExceptionWasThrown = true;
            } else {
                throw new RuntimeException(t);
            }
        }
        Assert.assertTrue(anticipatedExceptionWasThrown);
    }

    
    @Test
    public void productionServerBug_2018_11_16_ii() {
        /*
         *    corr-id: sse-1542399917
         *
         *    P R O V E N A N C E    ,    R E S O L U T I O N    a n d    F I X
         *
         * [1] Email from [Tara Gokas] to [cycledev]                                 on 2018-11-16_T_1218 subject "Re: [cycledev] CPS: another user-reported issue"
         * [2] Email from [Tara Gokas] to [Menelaos Perdikeas]                       on 2018-11-16_T_1219 subject "Screenshot"
         * [3] Email from [Menelaos Perdikeas] to [Tara Gokas], [cycledev], [arcdev] on 2018-11-16_T_1237 subject "Re: Screenshot"
         * 
         * Note 1: The user subsequently edited his ORCID profile to include publicly visible given names (first and last); as such
         *         the below test no longer accomplishes anything. It is only left here as a record and, perhaps, a curiosity.
         *         I' ve tested with my own ORCID (0000-0003-3031-1435) and can reproduce this (and thus verify the fix) when I set
         *         the privacy setting on the ORCID web site (https://orcid.org/my-orcid) to 'only me'. At the time of this writing
         *         there are three privacy settings on the ORCID web site in response to the 'who can see this?' question: 'everyone',
         *         'trusted third parties' and 'only me'. So this problem was on account of the user having set the visibility setting
         *         (for their first and last name!) to 'only me'.
         * Note 2: Moreover, the problem was a NPE in *JavaScript* code in the signup screen, so again the below test is not really relevant
         *         and is only useful as a record of this affair. The actual exception captured in the browser's developer window was the
         *         following (in Chrome but, really, browser-independent):
         *
         *         ---%<------------------------------------------------------------------------------------------------------
         *            VM65 signup-edit-account-common.js:63 Uncaught TypeError: Cannot read property 'given-names' of null
         *               at Object.success (VM65 signup-edit-account-common.js:63)
         *               at i (VM42 jquery-3.2.1.min.js:2)
         *               at Object.fireWith [as resolveWith] (VM42 jquery-3.2.1.min.js:2)
         *               at A (VM42 jquery-3.2.1.min.js:4)
         *               at XMLHttpRequest.<anonymous> (VM42 jquery-3.2.1.min.js:4)
         *         ------------------------------------------------------------------------------------------------------>%---
         *
         * Note 3: At any rate, this was totally unrelated to what the user and Tara originally reported. I.e. the popup dialog asking
         *         the user to confirm their "unusual" institution didn't have anything to do with the observed failure, not even
         *         tangentially; no causation whatsoever.
         *
         * Note 4: The fix is in file:
         *
         *             vobs/ASC_DB_WEB/src/db/www/webapps/cxcaccount/webgui/web/signup-edit-account-common.js
         *
         *         ... under the same correlation identifier (corr-id).
         * 
         */
        try {
            final String ORCID = "0000-0002-1061-1804";
            Assert.assertTrue(ORCIDValidator.isWellFormed(ORCID));
            final ORCIDPerson x = orcidRetriever.getPerson(ORCID);
            System.out.printf("person is: \n%s\n", x);
        } catch (Throwable t) {
            final Class TOLERATED_EXCEPTION_CLASS = CannotConstructORCIDPersonFromJson.class;            
            final Class exceptionClass = t.getClass();
            if (exceptionClass.equals(TOLERATED_EXCEPTION_CLASS))
                ; // do nothing, we tolerate this particular exception
            else
                throw new RuntimeException(t);
        }
    }


    
    @Test
    public void testWithMenelaosPerdikeasORCID() {
        try {
            final String ORCID = "0000-0003-3031-1435";
            Assert.assertTrue(ORCIDValidator.isWellFormed(ORCID));
            final ORCIDPerson x = orcidRetriever.getPerson(ORCID);
            System.out.printf("person is: \n%s\n", x);
        } catch (Throwable t) {
            final Class TOLERATED_EXCEPTION_CLASS = CannotConstructORCIDPersonFromJson.class;            
            final Class exceptionClass = t.getClass();
            if (exceptionClass.equals(TOLERATED_EXCEPTION_CLASS))
                ; // do nothing, we tolerate this particular exception
            else
                throw new RuntimeException(t);
        }
    }

    @Test
    public void testWithMenelaosPerdikeasORCID_version_with_captured_JSON_response() {
        final Class ANTICIPATED_EXCEPTION_CLASS = CannotConstructORCIDPersonFromJson.class;
        boolean anticipatedExceptionWasThrown = false;
        try {
            final String json = Files.toString(new File("test/test-files/orcid/0000-0003-3031-1435.json"), StandardCharsets.UTF_8);
            final ORCIDPerson x = ORCIDPerson.fromJson(json);
        } catch (Throwable t) {
            final Class exceptionClass = t.getClass();
            if (exceptionClass.equals(ANTICIPATED_EXCEPTION_CLASS)) {
                System.out.printf("encountered anticipated exception: [%s]; all's well!"
                                  , exceptionClass.getSimpleName());
                anticipatedExceptionWasThrown = true;
            } else {
                throw new RuntimeException(t);
            }
        }
        Assert.assertTrue(anticipatedExceptionWasThrown);
    }
    
}
