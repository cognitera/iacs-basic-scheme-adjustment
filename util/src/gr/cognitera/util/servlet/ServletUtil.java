package gr.cognitera.util.servlet;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Set;

import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import javax.ws.rs.core.UriBuilder;

import org.junit.Assert;
import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.base.Joiner;
    
import gr.cognitera.util.base.StringUtil;
import gr.cognitera.util.base.CollectionUtil;

import gr.cognitera.util.base.StringUtil;

public final class ServletUtil {

    private static Logger logger = Logger.getLogger(ServletUtil.class);

    private ServletUtil() {}
    
    public static String getFullURL(final HttpServletRequest request) {
        final StringBuffer requestURLSB = request.getRequestURL();
        final String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURLSB.toString();
        } else {
            return requestURLSB.append('?').append(queryString).toString();
        }
    }

    /**
     * Returns the 'file' part of the URL that the client used to make this request. The term 'file'
     * alludes to the method URL#getFile and has the same semantics.
     */
    private static String getFilePartOfURLThatClientUsed(final HttpServletRequest req, final Logger _logger) {
        final Logger logger = _logger==null?ServletUtil.logger:_logger;
        String rvMethod1 = null;
        {
            final String contextPath = req.getContextPath();
            logger.trace(String.format("getFilePartOfURLThatClientUsed() # contextPath is: [%s]", contextPath));
            Assert.assertTrue( (contextPath.equals("")) || (contextPath.startsWith("/") && !contextPath.endsWith("/")) );
            final String servletPath = req.getServletPath();
            logger.trace(String.format("getFilePartOfURLThatClientUsed() # servletPath is: [%s]", servletPath));
            Assert.assertTrue( (servletPath.equals("")) || (servletPath.startsWith("/") && !servletPath.endsWith("/")) );
            final String pathInfo    = req.getPathInfo();
            logger.trace(String.format("getFilePartOfURLThatClientUsed() # pathInfo is: [%s]", pathInfo));
            Assert.assertTrue( (pathInfo==null) || pathInfo.startsWith("/") );
            final String queryString = req.getQueryString();
            logger.trace(String.format("getFilePartOfURLThatClientUsed() # queryString is: [%s]", queryString));

            rvMethod1 = String.format("%s%s%s%s"
                                      , contextPath
                                      , servletPath
                                      , (pathInfo==null)?"":pathInfo // pathInfo, if present will start with '/' (we have that from the JavaDoc) so no need to agonize here
                                      , ((queryString==null)||"".equals(queryString))?"":String.format("?%s", queryString));

        }
        String rvMethod2 = null;
        {
            try {
                URL fullURL = new URL(getFullURL(req));
                rvMethod2 = fullURL.getFile();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        Assert.assertEquals(rvMethod1, rvMethod2);
        logger.debug(String.format("getFilePartOfURLThatClientUsed() # returning: [%s]"
                                   , rvMethod1));
        return rvMethod1;
    }

    public static String getURLUpToContextPathTakingXForwardedHeadersIntoAccount(final HttpServletRequest req, final Logger _logger) {
        final Logger logger = _logger==null?ServletUtil.logger:_logger;
        String fullURLTakingXForwardedHeadersIntoAccount = getFullURLTakingXForwardedHeadersIntoAccount(req, logger);
        try {
            URL fullURL = new URL(fullURLTakingXForwardedHeadersIntoAccount);
            if (fullURL.getPort()==-1)
                return String.format("%s://%s%s"
                                     , fullURL.getProtocol()
                                     , fullURL.getHost()
                                     , req.getContextPath());
            else
                return String.format("%s://%s:%d%s"
                                     , fullURL.getProtocol()
                                     , fullURL.getHost()
                                     , fullURL.getPort()
                                     , req.getContextPath());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFullURLTakingXForwardedHeadersIntoAccount(final HttpServletRequest req, final Logger _logger) {
        final Logger logger = _logger==null?ServletUtil.logger:_logger;
        final String urlS = getFullURL(req);
        Set<String> xForwardedHostUniqueS  ;
        Set<String> xForwardedProtoUniqueS ;
        Set<String> xForwardedPortUniqueS  ;
        final String X_FORWARDED_HOST  = "x-forwarded-host";
        final String X_FORWARDED_PROTO = "x-forwarded-proto";
        final String X_FORWARDED_PORT  = "x-forwarded-port";
        {
            final List<String> xForwardedHostS  = Collections.list(req.getHeaders(X_FORWARDED_HOST  ));
            final List<String> xForwardedProtoS = Collections.list(req.getHeaders(X_FORWARDED_PROTO ));
            final List<String> xForwardedPortS  = Collections.list(req.getHeaders(X_FORWARDED_PORT  ));

            xForwardedHostUniqueS  = CollectionUtil.uniqueValues(xForwardedHostS);
            xForwardedProtoUniqueS = CollectionUtil.uniqueValues(xForwardedProtoS);
            xForwardedPortUniqueS  = CollectionUtil.uniqueValues(xForwardedPortS);                
            logger.trace(String.format("[%d] %s headers (%d unique), [%d] %s headers (%d unique), [%d] %s headers (%d unique)"
                                       , xForwardedHostS       .size()
                                       , X_FORWARDED_HOST
                                       , xForwardedHostUniqueS .size()
                                       , xForwardedProtoS      .size()
                                       , X_FORWARDED_PROTO
                                       , xForwardedProtoUniqueS.size()
                                       , xForwardedPortS       .size()
                                       , X_FORWARDED_PORT
                                       , xForwardedPortUniqueS .size()));
        }
        if ((xForwardedHostUniqueS .size()==1) &&
            (xForwardedProtoUniqueS.size()==1) &&
            (xForwardedPortUniqueS .size()==1)) {
            String xForwardedHost  = CollectionUtil.exactlyOne(xForwardedHostUniqueS);
            String xForwardedProto = CollectionUtil.exactlyOne(xForwardedProtoUniqueS);
            String xForwardedPort  = CollectionUtil.exactlyOne(xForwardedPortUniqueS);
            logger.trace(String.format("the three X-Forwarded-* headers of interest contain a single unique field each: %s=[%s], %s=[%s], %s=[%s]"
                                       , X_FORWARDED_HOST
                                       , xForwardedHost
                                       , X_FORWARDED_PROTO
                                       , xForwardedProto
                                       , X_FORWARDED_PORT
                                       , xForwardedPort));
            final Set<String> xForwardedHostCSVs  = CollectionUtil.uniqueValues(StringUtil.commaSeparatedValues(xForwardedHost , true));
            final Set<String> xForwardedProtoCSVs = CollectionUtil.uniqueValues(StringUtil.commaSeparatedValues(xForwardedProto, true));
            final Set<String> xForwardedPortCSVs  = CollectionUtil.uniqueValues(StringUtil.commaSeparatedValues(xForwardedPort , true));
            if ((xForwardedHostCSVs .size()==1) &&
                (xForwardedProtoCSVs.size()==1) &&
                (xForwardedPortCSVs .size()==1)) {
                logger.trace("no multiple CSV values");
                xForwardedHost  = CollectionUtil.exactlyOne(xForwardedHostCSVs);
                xForwardedProto = CollectionUtil.exactlyOne(xForwardedProtoCSVs);
                xForwardedPort  = CollectionUtil.exactlyOne(xForwardedPortCSVs);
                Assert.assertTrue((xForwardedHost !=null) && StringUtil.isNeitherEmptyStringNorWhitespaceButMaybeNull(xForwardedHost));
                Assert.assertTrue((xForwardedProto!=null) && StringUtil.isNeitherEmptyStringNorWhitespaceButMaybeNull(xForwardedProto));
                Assert.assertTrue((xForwardedPort !=null) && StringUtil.isNeitherEmptyStringNorWhitespaceButMaybeNull(xForwardedPort));
                if (StringUtil.isInteger(xForwardedPort)) {
                    final int port = Integer.parseInt(xForwardedPort);
                    try {
                        final URL url = new URL(urlS);
                        final String protoOrig = url.getProtocol();
                        final String hostOrig  = url.getHost();
                        final int    portOrig  = url.getPort();
                        logger.trace(String.format("Protocol was [%s]; we'll use [%s]. Host was [%s]; we'll use [%s]. Port was [%d]; we'll use [%d]."
                                                   , protoOrig
                                                   , xForwardedProto
                                                   , hostOrig
                                                   , xForwardedHost
                                                   , portOrig
                                                   , port));
                        final String file  = getFilePartOfURLThatClientUsed(req, logger);
                        logger.info(String.format("file is: [%s]", file));
                        Assert.assertTrue(file.startsWith("/"));
                        if (xForwardedProto.equalsIgnoreCase("http") && (port==80))
                            return String.format("http://%s%s"
                                                 , xForwardedHost
                                                 , file);
                        if (xForwardedProto.equalsIgnoreCase("https") && (port==443))
                            return String.format("https://%s%s"
                                                 , xForwardedHost
                                                 , file);
                        return String.format("%s://%s:%d%s"
                                             , xForwardedProto
                                             , xForwardedHost
                                             , port
                                             , file);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    logger.warn(String.format("The value of the [%s] header (%s) is not an integer, aborting use of X-Forwarded-* headers"
                                              , X_FORWARDED_PORT
                                              ,  xForwardedPort));
                }
            } else {
                logger.warn(String.format("zero or multiple (and non-identical) CSV values detected in at least one of the X-Forwarded-* headers; aborting use of all X-Forwarded-* headers."+
                                          " In particular: "+
                                          "the [%s] header (even though unique / distinct as a whole) had [%d] distinct CSV values: [%s]%s"+ ", "   +
                                          "the [%s] header (even though unique / distinct as a whole) had [%d] distinct CSV values: [%s]%s"+ " and "+
                                          "the [%s] header (even though unique / distinct as a whole) had [%d] distinct CSV values: [%s]%s"+ "."
                                          , X_FORWARDED_HOST , xForwardedHostCSVs .size(), Joiner.on(", ").join(xForwardedHostCSVs) , xForwardedHostCSVs .size()==1?"":" (!!!???)"
                                          , X_FORWARDED_PROTO, xForwardedProtoCSVs.size(), Joiner.on(", ").join(xForwardedProtoCSVs), xForwardedProtoCSVs.size()==1?"":" (!!!???)"
                                          , X_FORWARDED_PORT , xForwardedPortCSVs .size(), Joiner.on(", ").join(xForwardedPortCSVs) , xForwardedPortCSVs .size()==1?"":" (!!!???)"));
            }
        } else {
            logger.warn(String.format("zero or multiple (and non-identical) header values for at least one of the X-Forwarded-* headers; aborting use of all X-Forwarded-* headers."+
                                      " In particular, there were: "+
                                      "[%d] distinct [%s] headers: [%s]%s"+ ", "   +
                                      "[%d] distinct [%s] headers: [%s]%s"+ " and "+
                                      "[%d] distinct [%s] headers: [%s]%s"+ "."
                                      , xForwardedHostUniqueS .size(), X_FORWARDED_HOST , Joiner.on(", ").join(xForwardedHostUniqueS) , xForwardedHostUniqueS .size()==1?"":"(!!!???)"
                                      , xForwardedProtoUniqueS.size(), X_FORWARDED_PROTO, Joiner.on(", ").join(xForwardedProtoUniqueS), xForwardedProtoUniqueS.size()==1?"":"(!!!???)"
                                      , xForwardedPortUniqueS .size(), X_FORWARDED_PORT , Joiner.on(", ").join(xForwardedPortUniqueS) , xForwardedPortUniqueS .size()==1?"":"(!!!???)"));
        }
        logger.warn(String.format("Not relying on X-Forwarded-* headers, returning [%s]", urlS));
        return urlS;
    }

    public static String fullURLupToContextPath(final HttpServletRequest req) {
        final String url = String.format("%s://%s:%d%s"
                                         , req.getScheme()
                                         , req.getServerName()
                                         , req.getServerPort()
                                         , req.getContextPath());
        return url;
    }    

    public static Cookie getCookie(final HttpServletRequest req, final String cookieName) {
        final Cookie[] cookies = req.getCookies();
        if (cookies==null)
            return null;
        final List<Cookie> rv = new ArrayList<>();
        for (int i = 0; i < cookies.length; i++) {
            if (cookieName.equals(cookies[i].getName()))
                rv.add(cookies[i]);
        }
        if (rv.size()>1)
            throw new RuntimeException(String.format("%d cookies with name [%s] found"
                                                     , rv.size()
                                                     , cookieName));
        else if (rv.size()==0)
            return null;
        else
            return rv.get(0);
    }

    @Deprecated // signature needs to change as per: http://stackoverflow.com/a/11103048/274677
    public static void deleteCookie(final HttpServletRequest req, final HttpServletResponse res, final String cookieName, final boolean assertThatCookieExists) {
        final Cookie cookie = getCookie(req, cookieName);
        if (cookie!=null) {
            cookie.setValue(null);
            cookie.setMaxAge(0);
            res.addCookie(cookie);
            logger.info(String.format("added cookie [%s] to response", cookie));
        } else {
            if (assertThatCookieExists)
                throw new RuntimeException("expected cookie to exist, and yet it doesn't");
        }
    }

    public static Map<String, List<String>> parseQuery(final String queryString) {
        try {
            final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
            final String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                final int idx = pair.indexOf('=');
                final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name()) : pair;
                if (!query_pairs.containsKey(key)) {
                    query_pairs.put(key, new ArrayList<String>());
                }
                final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name()) : null;
                query_pairs.get(key).add(value);
            }
            return query_pairs;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean queryContainsParameter(final String queryString, final String parameter) {
        return parseQuery(queryString).containsKey(parameter);
    }

    public static URI removeParameterFromURI(final URI uri
                                             , final String parameter
                                             , final boolean assertAtLeastOneIsFound
                                             , final Integer assertHowManyAreExpected) {
        if ((assertHowManyAreExpected!=null) && (assertHowManyAreExpected<=0))
            throw new IllegalArgumentException("it makes no sense to expect 0 or less");
        if ((!assertAtLeastOneIsFound) && (assertHowManyAreExpected!=null))
            throw new IllegalArgumentException("it makes no sense to not assert that at least one is found and at the same time assert a definite expected number");
        final String queryString = uri.getRawQuery();
        if (queryString==null)
            return uri;
        final Map<String, List<String>> params = parseQuery(queryString);
        logger.info(String.format("Query:\n%s\n...parsed as:\n%s\n"
                                  , queryString
                                  , StringUtil.stringify(params)));
        final Map<String, List<String>> paramsModified = new LinkedHashMap<>();
        boolean found = false;
        for (Map.Entry<String, List<String>> entry: params.entrySet()) {
            final String       key   = entry.getKey();
            final List<String> value = entry.getValue();
            if (!key.equals(parameter))
                Assert.assertNull(paramsModified.put(key, value));
            else {
                found = true;
                if (assertHowManyAreExpected!=null) {
                    Assert.assertEquals((long) assertHowManyAreExpected, value.size());
                }
            }
        }
        if (assertAtLeastOneIsFound)
            Assert.assertTrue(found);

        final org.apache.http.client.utils.URIBuilder uriBuilder = new org.apache.http.client.utils.URIBuilder(uri);
        uriBuilder.clearParameters();
        for (final Map.Entry<String, List<String>> entry: paramsModified.entrySet()) {
            final String       key    = entry.getKey();
            final List<String> values = entry.getValue();
            for (final String value: values)
                uriBuilder.addParameter(key, value);
        }
        try {
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    public static String removeParameterFromURI(final String uri, final String parameter, final boolean assertAtLeastOneIsFound, final Integer assertHowManyAreExpected) {
        try {
            return removeParameterFromURI(new URI(uri), parameter, assertAtLeastOneIsFound, assertHowManyAreExpected).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String extractURLUpToContextPathOnly(final String urlS) throws MalformedURLException {
        final URL url = new URL(urlS);
        final String path = url.getPath();
        final String contextPath = path.split("/")[1];
        return String.format("%s://%s/%s"
                             , url.getProtocol()
                             , url.getAuthority()
                             , contextPath);
    }

    /**
     * picks up, using an XPath expression, any value in a web application's web.xml
     *
     * @param servletContext  the servlet context for the application
     * @param xpathExpr  the XPath expression to evaluate in the deployment descriptor (web.xml)
     *               file of the application
     * @return the evaluation result
     *
     */
    public static String readFromDeploymentDescriptor(final ServletContext servletContext
                                                      , final String xpathExpr)
        throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        final XPathExpression xpath = XPathFactory.newInstance().newXPath().compile(xpathExpr);
        final InputStream webXmlIS = servletContext.getResourceAsStream("/WEB-INF/web.xml");
        final Document webXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(webXmlIS);
        return xpath.evaluate(webXmlDocument);
    }

    /**
     * same as {@link #readFromDeploymentDescriptor} but using the unchecked exception idiom
     * <p>
     * this method simply delegates to {@link #readFromDeploymentDescriptor} and wraps the call
     * in a try-catch block using the unchecked exception idiom (i.e. converting the checked
     * to an unchecked exception).
     *
     */
    public static String readFromDeploymentDescriptorUNCEXC(final ServletContext servletContext
                                                            , final String xpathExpr) {
        try {
            return readFromDeploymentDescriptor(servletContext, xpathExpr);
        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int sessionTimeoutFromDeploymentDescriptor(final ServletContext servletContext) {
        return Integer.parseInt(readFromDeploymentDescriptorUNCEXC(servletContext
                                                                   , "web-app/session-config/session-timeout"));
    }
}
