package gr.cognitera.util.servlet;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import javax.servlet.ServletContext;
    
import org.junit.Assert;
import org.apache.log4j.Logger;


import gr.cognitera.util.rest.JsonSimpleRestClient;
import gr.cognitera.util.rest.HttpStatusNotOKish;
import gr.cognitera.util.rest.NameValuePairHelper;

public class GoogleCaptchaResponseChecker implements IGoogleCaptchaResponseChecker {

    final static private Logger _logger                         = Logger.getLogger(GoogleCaptchaResponseChecker.class);
    final static private String DEFAULT_GOOGLE_VERIFICATION_URL = "https://www.google.com/recaptcha/api/siteverify";

    private String googleVerificationURL;
    private Logger logger;

    public GoogleCaptchaResponseChecker() {
        this(DEFAULT_GOOGLE_VERIFICATION_URL);
    }
    
    public GoogleCaptchaResponseChecker(final String googleVerificationURL) {
        this(googleVerificationURL, _logger);
    }

    public GoogleCaptchaResponseChecker(final Logger logger) {
        this(DEFAULT_GOOGLE_VERIFICATION_URL, logger);
    }    

    public GoogleCaptchaResponseChecker(final String googleVerificationURL, final Logger logger) {
        this.googleVerificationURL = googleVerificationURL;
        this.logger                = logger;
    }

    public List<String> captchaVerificationErrorCodes(final String secretKey, final String remoteIP, final String gRecaptchaResponse) {
        return GoogleCaptchaResponseChecker.captchaVerificationErrorCodes(googleVerificationURL, secretKey, remoteIP, gRecaptchaResponse, logger);
    }

    public static List<String> captchaVerificationErrorCodes(final String googleVerificationURL, final String secretKey, final String remoteIP, final String gRecaptchaResponse) {
        return captchaVerificationErrorCodes(googleVerificationURL, secretKey, remoteIP, gRecaptchaResponse, _logger);
    }

    public static List<String> captchaVerificationErrorCodes(final String googleVerificationURL, final String secretKey, final String remoteIP, final String gRecaptchaResponse, final Logger logger) {
        try {
            GoogleCaptchaResponse googleCaptchaResponse = JsonSimpleRestClient._post(googleVerificationURL
                                                                                    , NameValuePairHelper.create(    "secret", secretKey
                                                                                                                 , "response", gRecaptchaResponse
                                                                                                                 , "remoteip", remoteIP)
                                                                                    , GoogleCaptchaResponse.class
                                                                                    , false);
            Assert.assertTrue(  (  googleCaptchaResponse.success && googleCaptchaResponse.errorCodes==null)                                                   ||
                                ((!googleCaptchaResponse.success && googleCaptchaResponse.errorCodes!=null) && (!googleCaptchaResponse.errorCodes.isEmpty())) );
            logger.debug(String.format("Recaptcha response was: %s", googleCaptchaResponse));
            if (!googleCaptchaResponse.success)
                logger.debug(String.format("Recaptcha to [%s] returned %s"
                                           , googleVerificationURL
                                           , googleCaptchaResponse));
            if (googleCaptchaResponse.errorCodes==null)
                return new ArrayList<String>();
            else {
                Assert.assertFalse(googleCaptchaResponse.errorCodes.isEmpty());
                return googleCaptchaResponse.errorCodes;
            }
        } catch (IOException | HttpStatusNotOKish e) {
            throw new RuntimeException(e);
        }
    }
}

