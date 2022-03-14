package gr.cognitera.util.rest;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.IOException;

import org.junit.Assert;

import org.apache.http.Header;
import org.apache.http.NameValuePair;

import org.apache.http.util.EntityUtils;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;

import gr.cognitera.util.gson.GsonHelper;
import gr.cognitera.util.gson.NoAddedValueJSONWrapper;



public final class JsonSimpleRestClientUtil {
    private JsonSimpleRestClientUtil() {}

     public static final void jsonRESTPostReturningVoid(final String target, List<NameValuePair> nvps, final Logger logger, final Header ...headers) throws IOException, HttpStatusNotOKish, InternalServerException {
        final Type TYPE = new TypeToken<ValueOrInternalServerExceptionData<Void>>() {}.getType();
        ValueOrInternalServerExceptionData<Void> rv = JsonSimpleRestClient._post(target, nvps, TYPE, logger, headers);
        Assert.assertNotNull(rv);
        ValueOrInternalServerExceptionUtils.doNothingUnlessException(rv);
    }

    public static final Integer jsonRESTPostReturningInteger(final String target, List<NameValuePair> nvps, final Logger logger, final Header ... headers) throws IOException, HttpStatusNotOKish, InternalServerException {
        final Type TYPE = new TypeToken<ValueOrInternalServerExceptionData<Integer>>() {}.getType();
        ValueOrInternalServerExceptionData<Integer> rv = JsonSimpleRestClient._post(target, nvps, TYPE, logger, headers);
        Assert.assertNotNull(rv);
        return ValueOrInternalServerExceptionUtils.<Integer>returnValue(rv);
    }



}
