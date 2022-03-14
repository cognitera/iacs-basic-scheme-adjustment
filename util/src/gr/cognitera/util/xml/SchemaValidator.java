package gr.cognitera.util.xml;

import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import org.junit.Assert;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;


public final class SchemaValidator {

    private SchemaValidator() {}

    public static final Exception validate(final Source xml
                                              , final Source xsd) {
        return validate(xml, xsd, (LSResourceResolver) null);

    }

    public static final Exception validate(final Source xml
                                           , final Source xsd
                                           , final LSResourceResolver resourceResolver) {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        if (resourceResolver!=null)
            schemaFactory.setResourceResolver(resourceResolver);
        Schema schema = null;
        try {
            schema = schemaFactory.newSchema(xsd);
        } catch (SAXException e) {
            /*  Any exception caught at this point points to something wrong in the wiring
             *  and is not a bona fide XSD validation error - as such, we throw an unchecked exception
             */
            throw new RuntimeException(e);
        }
        Assert.assertNotNull(schema);
        final Validator validator = schema.newValidator();
        try {
            validator.validate(xml);
            return null;
        } catch (SAXException | IOException e) {
            /*  Any exception caught at this point is a legitimate XSD validation exception
             *  so we return it as a value. NB: I have actually encountered cases where an
             *  XML well-formed document gave rise, during XSD validation to a MalformedURLException
             *  (which is an IOException subclass) because a URL that appeared as the value of a
             *   property in that document was garbled - see: sse-1548267184 (in test cases)
             */
            return e;
        } 
    }
}
