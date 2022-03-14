package gr.cognitera.util.orcid;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;

import org.apache.http.Header;

import org.junit.Assert;
import org.apache.http.message.BasicHeader;

import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
                        

import gr.cognitera.util.rest.SimpleRestClient;
import gr.cognitera.util.rest.HttpStatusNotOKish;
    
public class ORCIDUtil {

    final static Logger _logger = Logger.getLogger(ORCIDUtil.class);

    private ORCIDUtil() {}

    public static Header orcidAcceptHeader() {
        return new BasicHeader("Accept", "application/vnd.orcid+json");
    }

}
