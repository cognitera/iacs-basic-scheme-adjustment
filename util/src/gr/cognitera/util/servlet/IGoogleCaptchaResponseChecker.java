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

public interface IGoogleCaptchaResponseChecker {

    List<String> captchaVerificationErrorCodes(String secretKey, String remoteIP, String gRecaptchaResponse);
 
}

