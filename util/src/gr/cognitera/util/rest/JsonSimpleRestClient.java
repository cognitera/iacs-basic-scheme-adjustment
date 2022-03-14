package gr.cognitera.util.rest;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.IOException;


import org.apache.http.Header;
import org.apache.http.NameValuePair;

import org.apache.http.util.EntityUtils;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;

import gr.cognitera.util.base.StringUtil;

import gr.cognitera.util.gson.GsonHelper;
import gr.cognitera.util.gson.NoAddedValueJSONWrapper;



public final class JsonSimpleRestClient {

    final static Logger _logger = Logger.getLogger(JsonSimpleRestClient.class);

    private final Logger           logger; // see comment in SimpleRestClient.java (sse-1507218219)
    private final SimpleRestClient client;
    
    private static class NoAddedValueTypeToken<T> extends TypeToken<NoAddedValueJSONWrapper<T>> {

    }
    

    public JsonSimpleRestClient(final Logger logger, final List<Header> headers) {
        this.logger  = logger;
        this.client = new SimpleRestClient(logger, headers);
    }

    public static <T> T _get(final String target, final Class<T> klass, final boolean unwrap) throws IOException, HttpStatusNotOKish {
        return _get(target, klass, unwrap, _logger);
    }
    
    public static <T> T _get(final String target, final Class<T> klass, final boolean unwrap, final Logger logger, final Header ...headers) throws IOException, HttpStatusNotOKish {
        JsonSimpleRestClient jsonClient = new JsonSimpleRestClient(logger, new ArrayList<Header>(Arrays.asList(headers)));
        return jsonClient.get(target, klass, unwrap);
     }
    public <T> T get(String target, Class<T> klass, boolean unwrap) throws IOException, HttpStatusNotOKish {
        String json = client.get(target);
        if (logger!=null)
            logger.trace(String.format("%s#get: About to JSON: %s"
                                       , this.getClass().getName()
                                       , StringUtil.clipIfNecessary(json, 2000, 2000)));
        if (unwrap) {
            final Type TYPE = (new NoAddedValueTypeToken<T>()).getType();
            NoAddedValueJSONWrapper<T> rv = GsonHelper.fromJson(json, TYPE);
            if (logger!=null)
                logger.trace(String.format("%s#get: Unwrap case about to return: %s"
                                           , this.getClass().getName()
                                           , StringUtil.clipIfNecessary(rv.data.toString(), 2000, 2000)));
            return rv.data;
        } else {
            final T rv = GsonHelper.fromJson(json, klass);
            if (logger!=null)
                logger.trace(String.format("%s#get: Not unwrap case about to return: %s"
                                           , this.getClass().getName()
                                           , StringUtil.clipIfNecessary(rv.toString(), 2000, 2000)));
            return rv;
        }            
    }

    public static <T> T _get(final String target, final Type TYPE) throws IOException, HttpStatusNotOKish {
        return _get(target, TYPE, _logger);
    }
    public static <T> T _get(final String target, final Type TYPE, final Logger logger, final Header ...headers) throws IOException, HttpStatusNotOKish {
        JsonSimpleRestClient jsonClient = new JsonSimpleRestClient(logger, new ArrayList<Header>(Arrays.asList(headers)));
        return jsonClient.get(target, TYPE);
    }
    public <T> T get(String target, Type TYPE) throws IOException, HttpStatusNotOKish {
        String json = client.get(target);
        T rv = GsonHelper.fromJson(json, TYPE);
        return rv;
    }

    public static <T> T _post(final String target, final List<NameValuePair> nvps, final Class<T> klass, final boolean unwrap) throws IOException, HttpStatusNotOKish {
        return _post(target, nvps, klass, unwrap, _logger);
    }
    public static <T> T _post(final String target, final List<NameValuePair> nvps, final Class<T> klass, final boolean unwrap, final Logger logger, final Header ...headers) throws IOException, HttpStatusNotOKish {
        JsonSimpleRestClient jsonClient = new JsonSimpleRestClient(logger, new ArrayList<Header>(Arrays.asList(headers)));
        return jsonClient.post(target, nvps, klass, unwrap);
    }
    public <T> T post(final String target, final List<NameValuePair> nvps, final Class<T> klass, final boolean unwrap) throws IOException, HttpStatusNotOKish {
        logger.debug(String.format("%s#post: doing a post to [%s] and moving to the [%s] client"
                                  , this.getClass().getName()
                                  , target
                                  , client.getClass().getName()));
        final String json = client.post(target, nvps);
        if (unwrap) {
            final Type TYPE = (new NoAddedValueTypeToken<T>()).getType();
            NoAddedValueJSONWrapper<T> rv = GsonHelper.fromJson(json, TYPE);
            return rv.data;
        } else {
            return GsonHelper.fromJson(json, klass);
        }            
    }

    public static <T> T _post(final String target, final List<NameValuePair> nvps, final Type TYPE, final Header ...headers) throws IOException, HttpStatusNotOKish {
        return _post(target, nvps, TYPE, _logger, headers);
    }
    public static <T> T _post(final String target, final List<NameValuePair> nvps, final Type TYPE, final Logger logger, final Header ...headers) throws IOException, HttpStatusNotOKish {
        JsonSimpleRestClient jsonClient = new JsonSimpleRestClient(logger, new ArrayList<Header>(Arrays.asList(headers)));
        return jsonClient.post(target, nvps, TYPE);
    }
    public <T> T post(final String target, final List<NameValuePair> nvps, final Type TYPE) throws IOException, HttpStatusNotOKish {
        String json = client.post(target, nvps);
        T t = GsonHelper.fromJson(json, TYPE);
        return t;
    }
    
    public static <T> T _delete(final String target, final Class<T> klass, final boolean unwrap) throws IOException, HttpStatusNotOKish {
        return _delete(target, klass, unwrap, _logger);
    }
    public static <T> T _delete(final String target, final Class<T> klass, final boolean unwrap, final Logger logger, final Header ...headers) throws IOException, HttpStatusNotOKish {
        JsonSimpleRestClient jsonClient = new JsonSimpleRestClient(logger, new ArrayList<Header>(Arrays.asList(headers)));
        return jsonClient.delete(target, klass, unwrap);
    }
    public static <T> T _delete(final String target, final Type TYPE, final Logger logger, final Header ...headers) throws IOException, HttpStatusNotOKish {
        JsonSimpleRestClient jsonClient = new JsonSimpleRestClient(logger, new ArrayList<Header>(Arrays.asList(headers)));
        return jsonClient.delete(target, TYPE);
    }
    public <T> T delete(final String target, final Class<T> klass, final boolean unwrap) throws IOException, HttpStatusNotOKish {
        String json = client.delete(target);
        if (unwrap) {
            final Type TYPE = (new NoAddedValueTypeToken<T>()).getType();
            NoAddedValueJSONWrapper<T> rv = GsonHelper.fromJson(json, TYPE);
            return rv.data;
        } else {
            return GsonHelper.fromJson(json, klass);
        }            
    }
    public <T> T delete(final String target, final Type TYPE) throws IOException, HttpStatusNotOKish {
        String json = client.delete(target);
        T t = GsonHelper.fromJson(json, TYPE);
        return t;
    }
    
}
