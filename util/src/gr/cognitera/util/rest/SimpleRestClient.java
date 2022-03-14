package gr.cognitera.util.rest;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.net.URI;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.HttpHeaders;

import javax.xml.bind.DatatypeConverter;
// import org.apache.http.HttpHeaders;

import org.apache.log4j.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.Header;
import org.apache.http.NameValuePair;

import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpRequestBase;

import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.StringBody;

import org.apache.http.entity.ContentType;

import org.apache.commons.io.IOUtils;

import org.junit.Assert;

import com.google.common.base.Joiner;


import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;


import java.security.NoSuchAlgorithmException;
import java.security.KeyManagementException;

import gr.cognitera.util.base.StringUtil;
import gr.cognitera.util.base.NameValuePairUtil;
import gr.cognitera.util.base.StringClipper;
import gr.cognitera.util.rest.LaxRedirectStrategyWrapper;

class HttpsTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{};
    }
}


public class SimpleRestClient {

    final static Logger _logger = Logger.getLogger(SimpleRestClient.class);

    private final Logger       logger; // sse-1507218219: there is a valid use case of the logger passed as null (to disable logging for particular method calls). Therefore we can't just assert it to be non-null in the constructor and we have to check before each method call for NPE.
    private final List<Header> headers;

    public SimpleRestClient(final Logger logger, final List<Header> headers) {
        Assert.assertNotNull("headers may not be empty, pass empty List instead"
                             , headers);
        this.logger  = logger;
        this.headers = headers;
    }

    private static SSLConnectionSocketFactory TRUSTING_SSL_FACTORY;

    @SuppressWarnings("deprecation")
    private static SSLConnectionSocketFactory createTrustingSSLConnectionSocketFactory( ) {
        try {
            final SSLContext sslcontext = org.apache.http.conn.ssl.SSLContexts.custom().useSSL().build();
            sslcontext.init(null, new X509TrustManager[]{new HttpsTrustManager()}, new SecureRandom());

            return new SSLConnectionSocketFactory(sslcontext,
                                                  //  SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER
                                                  SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); // otherwise you may get: javax.net.ssl.SSLPeerUnverifiedException: Host name 'localhost' does not match the certificate subject provided by the peer (CN=mperdikeas-jboss-server, OU=mperdikeas-laptop, O=mperdikeas, L=planet Earth, ST=N/A, C=N/A)
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }        
    }

    static {
        TRUSTING_SSL_FACTORY = createTrustingSSLConnectionSocketFactory();
    }

    private static HttpClientBuilder getHTTPClientBuilderWithTrustingSSLContext() {
        return HttpClients.custom().setSSLSocketFactory(TRUSTING_SSL_FACTORY);
    }


    public static String _get(final String target) throws IOException, HttpStatusNotOKish {
        return _get(target, _logger);
    }
    public static String _get(final String target, final Logger logger, final Header ...headers) throws IOException, HttpStatusNotOKish {
        SimpleRestClient client = new SimpleRestClient(logger, new ArrayList<Header>(Arrays.asList(headers)));
        return client.get(target);
    }
    public String get(final String target) throws IOException, HttpStatusNotOKish {
        return get(target, (List<Header>) null);
    }
    public String get(final String target, final List<Header> additionalHeaders) throws IOException, HttpStatusNotOKish { // https://hc.apache.org/httpcomponents-client-ga/quickstart.html
            CloseableHttpClient httpClient = null;

            if (!headers.isEmpty())
                httpClient = getHTTPClientBuilderWithTrustingSSLContext().setDefaultHeaders(headers).build();
            else
                httpClient = getHTTPClientBuilderWithTrustingSSLContext().build();

            final HttpGet httpGet = new HttpGet(target);
            if (additionalHeaders!=null)
                for (final Header additionalHeader: additionalHeaders)
                    httpGet.setHeader(additionalHeader);
            return doHttpRequest(httpClient, httpGet);
    }


    public static String _post(final String target, final List<NameValuePair> _nvps) throws IOException, HttpStatusNotOKish {
        return _post(target, _nvps, _logger);
    }
    public static String _post(final String target, final List<NameValuePair> nvps, final Logger logger, final Header ...headers) throws IOException, HttpStatusNotOKish {
        SimpleRestClient client = new SimpleRestClient(logger, new ArrayList<Header>(Arrays.asList(headers)));
        return client.post(target, (List<Header>) null, nvps);
    }
    public String post(final String target
                       , final List<NameValuePair> nvps) throws IOException, HttpStatusNotOKish {
        return post(target, (List<Header>) null, nvps);
    }
    
    public String post(final String target
                       , final List<Header> additionalHeaders
                       , List<NameValuePair> nvps) throws IOException, HttpStatusNotOKish {
        if (nvps == null)
            nvps = new ArrayList<>();
        return postEntity(target, additionalHeaders, new UrlEncodedFormEntity(nvps), (String) null);
    }

    private static final String EXPECTED_CLASSNAME_OF_MULTIPART_ENTITY = "org.apache.http.entity.mime.MultipartFormEntity";

    public String postMultiPart(final String target
                                , final List<Header> additionalHeaders
                                , final List<NameValuePair> nvps
                                , final String name
                                , final File file) throws IOException, HttpStatusNotOKish {
        Assert.assertNotNull(String.format("the parameter [nvps] cannot be null, please use an empty List instead; yes, I know that this "+
                                           "contract is different than the one used in the 'post' method - deal with it")
                             , nvps);

        final String boundary = "---_~X~_-_~X~_-_~X~_-_~X~_-_~X~_--1527695432--";
        HttpEntity entity = null;
        {
            final MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create().setBoundary(boundary).setMode(HttpMultipartMode.STRICT);
            for (final NameValuePair nvp: nvps) {
                // I think only encoding values - not names - ought to be enough
                final String valueEncoded = URLEncoder.encode(nvp.getValue(), StandardCharsets.UTF_8.name());
                final boolean useAddPartOtherwiseUseaddTextBody = false; // based on (limited) testing it would appear that either true or false work
                if (useAddPartOtherwiseUseaddTextBody)
                    entityBuilder.addPart(nvp.getName(), new StringBody(nvp.getValue(), ContentType.TEXT_PLAIN));
                else
                    entityBuilder.addTextBody(nvp.getName(), nvp.getValue(), ContentType.TEXT_PLAIN);
                logger.debug(String.format("%s#postMultiPart: added [name, value] = [%s, %s]"
                                           , this.getClass().getName()
                                           , nvp.getName()
                                           , StringUtil.clipIfNecessary(valueEncoded, 2000, 2000)));
            }
            entityBuilder.addBinaryBody(name, file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
            entity = entityBuilder.build();
        }
        Assert.assertNotNull(entity);
        Assert.assertEquals(EXPECTED_CLASSNAME_OF_MULTIPART_ENTITY, entity.getClass().getName());

        if (logger!=null)
            logger.debug(String.format("%s#postMultiPart: executing multipart POST on [%s] with these form values: [%s] and reading from file [%s] under form name [%s]"
                                       , this.getClass().getName()
                                       , target
                                       , NameValuePairUtil.stringify(nvps
                                                                     , new StringClipper(2000, 2000))
                                       , file.getPath()
                                       , name));
        
        return postEntity(target, additionalHeaders, entity, boundary);
    }

    private String postEntity(final String target
                              , final List<Header> additionalHeaders
                              , final HttpEntity entity
                              , final String boundary) throws IOException, HttpStatusNotOKish {
        Assert.assertTrue( String.format("entity is of class [%s] that apparently is not one of the two expected types"
                                         , entity.getClass().getName())
                           , (((entity instanceof UrlEncodedFormEntity) && (boundary == null))
                              ||
                              (entity.getClass().getName().equals(EXPECTED_CLASSNAME_OF_MULTIPART_ENTITY))) ); // unfortunately I cannot do: 'instanceof MultipartFormEntity' as that class is not public in the package
        Assert.assertFalse(String.format("entity was of class [%s] (and so is an instance of [%s]), yet the boundary parameter was not null as expected"
                                         , entity.getClass().getName()
                                         , UrlEncodedFormEntity.class.getName())
                           , ((entity instanceof UrlEncodedFormEntity) && (boundary!=null)));
        Assert.assertFalse(String.format("entity is of class [%s], yet the boundary parameter was null"
                                         , entity.getClass().getName())
                           , (entity.getClass().getName().equals(EXPECTED_CLASSNAME_OF_MULTIPART_ENTITY) && (boundary==null) ) );
        Assert.assertFalse("It is an RFC requirement that the boundary must end with '--'"
                           , (boundary != null) && (!boundary.endsWith("--")));
        
        for (Header header: headers) {
            if (header.getName().equalsIgnoreCase(HttpHeaders.CONTENT_TYPE))
                throw new RuntimeException(String.format("The post method sets itw own [%s] header. Maybe I should allow the client to override "+
                                                         "that; I don't yet know as I haven't yet encountered such a use case. For the time "    +
                                                         "being I'll just panic"
                                                         , HttpHeaders.CONTENT_TYPE));
        }                   
        Header header = null;
        if (entity instanceof UrlEncodedFormEntity)
            header =  new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
        else if (entity.getClass().getName().equals(EXPECTED_CLASSNAME_OF_MULTIPART_ENTITY)) { 
            final String MULTIPART_CONTENT_TYPE=String.format("%s; boundary=%s"
                                                              , ContentType.MULTIPART_FORM_DATA.getMimeType()
                                                              , boundary);
            header =  new BasicHeader(HttpHeaders.CONTENT_TYPE, MULTIPART_CONTENT_TYPE);
        }
        else
            Assert.fail(String.format("Unhandled class for entity: [%s]", entity.getClass().getName()));
        Assert.assertNotNull(entity);
        final List<Header> headersCopy = new ArrayList<>(headers);
        headersCopy.add(header);
        final CloseableHttpClient httpClient = getHTTPClientBuilderWithTrustingSSLContext()
            .setDefaultHeaders(headersCopy)
            .setRedirectStrategy(new LaxRedirectStrategyWrapper(logger)) // this is to automatically redirect on POSTs when we receive 307
            .build();

        
        final HttpPost httpPost = new HttpPost(target);
        httpPost.setEntity(entity);
        if (additionalHeaders!=null)
            for (final Header additionalHeader: additionalHeaders)
                httpPost.setHeader(additionalHeader);
        if (logger!=null)
            logger.debug(String.format("%s#postEntity: relaying to doHttpRequest for target [%s]. Payload is: [%s]"
                                       , this.getClass().getName()
                                       , target
                                       , httpPost));
        return doHttpRequest(httpClient, httpPost);
    }
    


    public static String _delete(final String target) throws IOException, HttpStatusNotOKish {
        return _delete(target, _logger);
    }
    public static String _delete(final String target, final Logger logger, final Header ...headers) throws IOException, HttpStatusNotOKish {
        SimpleRestClient client = new SimpleRestClient(logger, new ArrayList<Header>(Arrays.asList(headers)));
        return client.delete(target);
    }
    public String delete(final String target) throws IOException, HttpStatusNotOKish {
        CloseableHttpClient httpClient = null;
        if (!headers.isEmpty()) httpClient = getHTTPClientBuilderWithTrustingSSLContext().setDefaultHeaders(headers).build();
        else                    httpClient = getHTTPClientBuilderWithTrustingSSLContext()                           .build();
        final HttpDelete httpDelete = new HttpDelete(target);
        return doHttpRequest(httpClient, httpDelete);
    }
    
    private String doHttpRequest(final CloseableHttpClient httpClient, final HttpRequestBase request) throws IOException, HttpStatusNotOKish {
        final String METHOD_NAME = "doHttpRequest";
        if (logger!=null)
            logger.debug(String.format("%s#%s: executing [%s]"
                                       , this.getClass().getName()
                                       , METHOD_NAME
                                       , request));
        
        CloseableHttpResponse response = httpClient.execute(request);
        // The underlying HTTP connection is still held by the response object
        // to allow the response content to be streamed directly from the network socket.
        // In order to ensure correct deallocation of system resources
        // the user MUST call CloseableHttpResponse#close() from a finally clause.
        // Please note that if response content is not fully consumed the underlying
        // connection cannot be safely re-used and will be shut down and discarded
        // by the connection manager.
        HttpEntity entity = null;
        try {
            if (logger!=null)
                logger.debug(String.format("%s#%s: response status line for request %s is: [%s]"
                                           , this.getClass().getName()
                                           , METHOD_NAME
                                           , request
                                           , response.getStatusLine()));
            entity = response.getEntity();
            String rv = IOUtils.toString(entity.getContent()
                                         , StandardCharsets.UTF_8);
            assertStatusOKish(request.getURI(), request.getMethod(), response.getStatusLine().getStatusCode(), rv);


            if (logger!=null)
                logger.trace(String.format("%s#%s: response for request %s was: [%s]"
                                           , this.getClass().getName()
                                           , METHOD_NAME
                                           , request
                                           , StringUtil.clipIfNecessary(rv, 2000, 2000)));
            return rv;
        } finally {
            // ... and ensure it is fully consumed            
            EntityUtils.consume(entity);             
            response.close();
        }
    }

    private static void assertStatusOKish(final URI uri, final String method, final int statusCode, final String response) throws HttpStatusNotOKish {
        if (!statusCodeIs2xx(statusCode))
            throw new HttpStatusNotOKish(uri, method, statusCode, response);
    }

    private static boolean statusCodeIs2xx(final int statusCode) {
        return ((statusCode>=200) && (statusCode<300));
    }

}

