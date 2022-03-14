package gr.cognitera.util.xml;

import org.junit.Assert;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
    
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.rules.ErrorCollector;
import org.junit.Rule;

import org.apache.log4j.Logger;

import org.xml.sax.SAXException;

import gr.cognitera.util.base.Util;
import gr.cognitera.util.base.StringUtil;
    

enum GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION {
    NONE, WIRING, LEGIT_XSD;

}


class ConstructorSupplier {
    private final String                                             xmlFname;
    private final String                                             xsdFname;
    private final CustomLoadResourceResolver                         customLoadResourceResolver;
    private final IResourceResolutionChecker                         resourceResolutionChecker;    
    private final GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION expectedSpeciesOfException;
    private final List<Class<? extends Throwable>>                   expectedExceptionChain;
    private final String                                             expectedStringInTopExceptionMessage;

    public ConstructorSupplier(final String xmlFname
                               , final String xsdFname
                               , final CustomLoadResourceResolver customLoadResourceResolver
                               , final IResourceResolutionChecker resourceResolutionChecker
                               , final GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION expectedSpeciesOfException
                               , final List<Class<? extends Throwable>> expectedExceptionChain
                               , final String expectedStringInTopExceptionMessage) {
        this.xmlFname                         = xmlFname;
        this.xsdFname                         = xsdFname;
        this.customLoadResourceResolver       = customLoadResourceResolver;
        this.resourceResolutionChecker        = resourceResolutionChecker;
        this.expectedSpeciesOfException       = expectedSpeciesOfException;
        this.expectedExceptionChain           = expectedExceptionChain;
        this.expectedStringInTopExceptionMessage = expectedStringInTopExceptionMessage;
        InitializationChecker.check(this.customLoadResourceResolver
                                    , this.resourceResolutionChecker
                                    , this.expectedSpeciesOfException
                                    , this.expectedExceptionChain
                                    , this.expectedStringInTopExceptionMessage);
    }


    public Object[] supplyArguments() {
        return new Object[]{xmlFname
                            , xsdFname
                            , customLoadResourceResolver
                            , resourceResolutionChecker
                            , expectedSpeciesOfException
                            , expectedExceptionChain
                            , expectedStringInTopExceptionMessage};
    }

}


@RunWith(Parameterized.class)
public class SchemaValidatorTest {

    private static final Logger logger = Logger.getLogger(SchemaValidatorTest.class);

    private final String                                             xmlFname;
    private final String                                             xsdFname;
    private final CustomLoadResourceResolver                         customLoadResourceResolver;
    private final IResourceResolutionChecker                         resourceResolutionChecker;    
    private final GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION expectedSpeciesOfException;
    private final List<Class<? extends Throwable>>                   expectedExceptionChain;
    private final String                                             expectedStringInTopExceptionMessage;


    public SchemaValidatorTest(final String xmlFname,
                               final String xsdFname,
                               final CustomLoadResourceResolver customLoadResourceResolver,
                               final IResourceResolutionChecker resourceResolutionChecker,
                               final GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION expectedSpeciesOfException,
                               final List<Class<? extends Throwable>> expectedExceptionChain,
                               final String                           expectedStringInTopExceptionMessage) {
        this.xmlFname = xmlFname;
        this.xsdFname = xsdFname;
        this.customLoadResourceResolver = customLoadResourceResolver;
        this.resourceResolutionChecker = resourceResolutionChecker;
        this.expectedSpeciesOfException = expectedSpeciesOfException;
        this.expectedExceptionChain = expectedExceptionChain;
        this.expectedStringInTopExceptionMessage = expectedStringInTopExceptionMessage;
        InitializationChecker.check(this.customLoadResourceResolver
                                    , this.resourceResolutionChecker
                                    , this.expectedSpeciesOfException
                                    , this.expectedExceptionChain
                                    , this.expectedStringInTopExceptionMessage);        
    }

    private static List<ConstructorSupplier> _data() {
        CustomLoadResourceResolver.ALLOW_NULL_OR_EMPTY_SYSTEMIDS_TO_PATHS = true;
        List<ConstructorSupplier> rv = new ArrayList<>();
        // index: 0
        rv.add(new ConstructorSupplier("00/a.xml"
                                       , "00/a.xsd"
                                       , (CustomLoadResourceResolver) null
                                       , (IResourceResolutionChecker) null
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.NONE
                                       , Arrays.asList(new Class[]{})
                                       , (String) null));
        // index: 1
        rv.add(new ConstructorSupplier("00/a.xml"
                                       , "00/a.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class, null)
                                       , new ResourceResolutionChecker()
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.NONE
                                       , Arrays.asList(new Class[]{})
                                       , (String) null));
        // index: 2
        rv.add(new ConstructorSupplier("00/a2.xml"
                                       , "00/a.xsd"
                                       , (CustomLoadResourceResolver) null
                                       , (IResourceResolutionChecker) null
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.NONE
                                       , Arrays.asList(new Class[]{})
                                       , (String) null));
        // index: 3
        rv.add(new ConstructorSupplier("00/a2.xml"
                                       , "00/a.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class, null)
                                       , new ResourceResolutionChecker()
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.NONE
                                       , Arrays.asList(new Class[]{})
                                       , (String) null));

        // index: 4
        rv.add(new ConstructorSupplier("01/a.xml"
                                       , "01/a.xsd"
                                       , (CustomLoadResourceResolver) null
                                       , (IResourceResolutionChecker) null
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.WIRING
                                       , Arrays.asList(new Class[]{RuntimeException.class, SAXException.class})
                                       , "Cannot resolve the name 'b:BType' to a(n) 'type definition' component."));
        // index: 5
        rv.add(new ConstructorSupplier("01/a.xml"
                                       , "01/a.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class, null)
                                       , new ResourceResolutionChecker(Arrays.asList(new String[]{"b.xsd"}), new ArrayList<String>())
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.WIRING
                                       , Arrays.asList(new Class[]{RuntimeException.class, SAXException.class})
                                       , "Cannot resolve the name 'b:BType' to a(n) 'type definition' component."));
        // index: 6        
        rv.add(new ConstructorSupplier("01/a.xml"
                                       , "01/a.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class,
                                                                        StringUtil.mapify("b.xsd=test-files/01/b.xsd"))
                                       , new ResourceResolutionChecker(Arrays.asList(new String[]{"b.xsd"})
                                                                       , Arrays.asList(new String[]{"test-files/01/b.xsd"}))
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.NONE
                                       , Arrays.asList(new Class[]{})
                                       , (String) null));
        // index: 7        
        rv.add(new ConstructorSupplier("01/a2.xml"
                                       , "01/a.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class,
                                                                        StringUtil.mapify("b.xsd=test-files/01/b.xsd"))
                                       , new ResourceResolutionChecker(Arrays.asList(new String[]{"b.xsd"})
                                                                       , Arrays.asList(new String[]{"test-files/01/b.xsd"}))
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.LEGIT_XSD
                                       , Arrays.asList(new Class[]{SAXException.class})
                                       , "cvc-datatype-valid.1.2.1: 'not-an-integer' is not a valid value for 'integer'"));
        // index: 8        
        rv.add(new ConstructorSupplier("02/objvis-sap-v0-orig.xml"
                                       , "02/VOTable-1.1.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class, null)
                                       , new ResourceResolutionChecker()
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.LEGIT_XSD
                                       , Arrays.asList(new Class[]{SAXException.class})
                                       , "cvc-elt.1: Cannot find the declaration of element 'VOTABLE'."));
        // index: 9        
        rv.add(new ConstructorSupplier("02/objvis-sap-v1-default-namespace.xml"
                                       , "02/VOTable-1.1.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class, null)
                                       , new ResourceResolutionChecker()
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.LEGIT_XSD
                                       , Arrays.asList(new Class[]{SAXException.class})
                                       , "cvc-enumeration-valid: Value '1.0' is not facet-valid with respect to"
                                       +" enumeration '[1.1]'. It must be a value from the enumeration."));
        // index: 10        
        rv.add(new ConstructorSupplier("02/objvis-sap-v2-fixed-version.xml"
                                       , "02/VOTable-1.1.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class, null)
                                       , new ResourceResolutionChecker()                                       
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.LEGIT_XSD
                                       , Arrays.asList(new Class[]{MalformedURLException.class}) // corr-id: sse-1548267184
                                       , (String) null));
        // index: 11
        rv.add(new ConstructorSupplier("02/objvis-sap-v3-fixed-schema_hint.xml"
                                       , "02/VOTable-1.1.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class, null)
                                       , new ResourceResolutionChecker()                                       
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.NONE
                                       , Arrays.asList(new Class[]{})
                                       , (String) null));
        // index: 12
        rv.add(new ConstructorSupplier("02/objvis-sap-v4-schema_hint-is-not-necessary.xml"
                                       , "02/VOTable-1.1.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class, null)
                                       , new ResourceResolutionChecker()                                       
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.NONE
                                       , Arrays.asList(new Class[]{})
                                       , (String) null));
        // index: 13
        rv.add(new ConstructorSupplier("03/objvis-sap-metadata-v0-orig.xml"
                                       , "03/VOTable-1.1.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class, null)
                                       , new ResourceResolutionChecker()                                       
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.LEGIT_XSD
                                       , Arrays.asList(new Class[]{SAXException.class})
                                       , "cvc-elt.1: Cannot find the declaration of element 'VOTABLE'."));
        // index: 14
        rv.add(new ConstructorSupplier("03/objvis-sap-metadata-v1-default-namespace.xml"
                                       , "03/VOTable-1.1.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class, null)
                                       , new ResourceResolutionChecker()                                       
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.LEGIT_XSD
                                       , Arrays.asList(new Class[]{SAXException.class})
                                       , "cvc-enumeration-valid: Value '1.0' is not facet-valid with respect to"
                                       +" enumeration '[1.1]'. It must be a value from the enumeration."));
        // index: 15
        rv.add(new ConstructorSupplier("03/objvis-sap-metadata-v2-fixed-version.xml"
                                       , "03/VOTable-1.1.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class, null)
                                       , new ResourceResolutionChecker()                                       
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.LEGIT_XSD
                                       , Arrays.asList(new Class[]{SAXException.class})
                                       , "cvc-complex-type.4: Attribute 'datatype' must appear on element 'PARAM'"));
        // index: 16
        rv.add(new ConstructorSupplier("03/objvis-sap-metadata-v3-datatype_attributes_added.xml"
                                       , "03/VOTable-1.1.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class, null)
                                       , new ResourceResolutionChecker()                                       
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.LEGIT_XSD
                                       , Arrays.asList(new Class[]{SAXException.class})
                                       , "cvc-enumeration-valid: Value 'foo' is not facet-valid with respect to"
                                       +" enumeration '[boolean, bit, unsignedByte, short, int, long, char,"
                                       +" unicodeChar, float, double, floatComplex, doubleComplex]'. It must be"
                                       +" a value from the enumeration."));
        // index: 17
        rv.add(new ConstructorSupplier("03/objvis-sap-metadata-v4-datatype_values_fixed.xml"
                                       , "03/VOTable-1.1.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class, null)
                                       , new ResourceResolutionChecker()                                       
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.LEGIT_XSD
                                       , Arrays.asList(new Class[]{SAXException.class})
                                       , "cvc-complex-type.4: Attribute 'value' must appear on element 'PARAM'"));
        // index: 18
        rv.add(new ConstructorSupplier("03/objvis-sap-metadata-v5-value-attrib-added.xml"
                                       , "03/VOTable-1.1.xsd"
                                       , new CustomLoadResourceResolver(SchemaValidatorTest.class, null)
                                       , new ResourceResolutionChecker()                                       
                                       , GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION.NONE
                                       , Arrays.asList(new Class[]{})
                                       , (String) null));
        return rv;
    }

    @Parameters(name = "{index}: {0} {1}}")
    public static Collection<Object[]> data() {
        List<Object[]> rv = new ArrayList<>();        
        for (ConstructorSupplier x: _data())
            rv.add(x.supplyArguments());
        return rv;
    }

    private Source filenameToSource(final String fname) throws IOException {
        final InputStream is = this.getClass().getResourceAsStream(String.format("test-files/%s", fname));
        return new StreamSource(is);
    }
    
    @Test
    public void test() {
        try {
            final Source xml = filenameToSource(xmlFname);
            final Source xsd = filenameToSource(xsdFname);

            switch (expectedSpeciesOfException) {
            case NONE:
                testForNoException(xml, xsd);
                break;
            case WIRING:
                testForWiringException(xml, xsd);
                break;
            case LEGIT_XSD:
                testForLegitimateXSDException(xml, xsd);
                break;
            default:
                Assert.fail(String.format("Unhandled case: [%s]", expectedSpeciesOfException));
            }
            if (resourceResolutionChecker!=null)
                resourceResolutionChecker.check(customLoadResourceResolver.resourceResolutionCoordinates
                                                , customLoadResourceResolver.resourceResolutionCoordinatesResolved);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Exception _validate(final Source xml, final Source xsd) {
        if (customLoadResourceResolver==null)
            return SchemaValidator.validate(xml, xsd);
        else
            return SchemaValidator.validate(xml, xsd, customLoadResourceResolver);
    }

    private void testForNoException(final Source xml, final Source xsd) {
        final Exception e = _validate(xml, xsd);
        Assert.assertNull(e);
    }

    private void testForWiringException(final Source xml, final Source xsd) {
        try {
            _validate(xml, xsd);            
            Assert.fail();
        } catch (Exception e) {
            final String ACTUAL_MESSAGE = e.getMessage();
            Assert.assertTrue(String.format("The actual message [%s] does not contain the"
                                            +" expected string [%s]"
                                            , ACTUAL_MESSAGE
                                            , expectedStringInTopExceptionMessage)
                              , ACTUAL_MESSAGE.contains(expectedStringInTopExceptionMessage));

            Throwable p = e;
            for (final Class<? extends Throwable> klass : expectedExceptionChain) {
                Assert.assertTrue(klass.isInstance(p));
                p = p.getCause();
            }
        }
    }


    private void testForLegitimateXSDException(final Source xml, final Source xsd) {
        Assert.assertTrue(expectedExceptionChain.size()==1);
        final Class<?> expectedExceptionClass = expectedExceptionChain.get(0);
        Exception exception = _validate(xml, xsd);
        Assert.assertNotNull(exception);
        final String ACTUAL_MESSAGE = exception.getMessage();
        if (expectedStringInTopExceptionMessage==null)
            Assert.assertNull(ACTUAL_MESSAGE);
        else {
            Assert.assertNotNull(ACTUAL_MESSAGE);
            Assert.assertTrue(expectedExceptionClass.isInstance(exception));
            Assert.assertTrue(String.format("The actual message [%s] does not contain the"
                                            +" expected string [%s]"
                                            , ACTUAL_MESSAGE
                                            , expectedStringInTopExceptionMessage)
                              , ACTUAL_MESSAGE.contains(expectedStringInTopExceptionMessage));
        }

    }
}


class InitializationChecker {
    static void check(final CustomLoadResourceResolver customLoadResourceResolver,
                      final IResourceResolutionChecker resourceResolutionChecker,
                      final GENUINE_WIRING_EXCEPTION_OR_LEGIT_XSDVAL_EXCEPTION expectedSpeciesOfException,
                      final List<Class<? extends Throwable>> expectedExceptionChain,
                      final String                           expectedStringInTopExceptionMessage) {
        Assert.assertTrue(Util.allAreNullOrNoneIsNull(customLoadResourceResolver, resourceResolutionChecker));
        Assert.assertNotNull(expectedExceptionChain);
        Assert.assertNotNull(expectedSpeciesOfException);
        switch (expectedSpeciesOfException) {
        case NONE:
            Assert.assertTrue(expectedExceptionChain.isEmpty());
            Assert.assertNull(expectedStringInTopExceptionMessage);
            break;
        case WIRING:
            Assert.assertFalse(expectedExceptionChain.isEmpty());
            Assert.assertNotNull(expectedStringInTopExceptionMessage);
            break;
        case LEGIT_XSD:
            Assert.assertEquals(expectedExceptionChain.size(), 1);
            break;
        default:
            Assert.fail(String.format("Unhandled case: [%s]", expectedSpeciesOfException));
        }
    }
}


interface IResourceResolutionChecker {

    void check(final List<ResourceResolutionCoordinates>           resourceResolutionCoordinates
               , final List<ResourceResolutionCoordinatesResolved> resourceResolutionCoordinatesResolved);

}


class ResourceResolutionChecker implements IResourceResolutionChecker {

    private final List<String> systemIds;
    private final List<String> resolutionPaths;

    public ResourceResolutionChecker() {
        this(new ArrayList<String>(), new ArrayList<String>());
    }
    public ResourceResolutionChecker(  final List<String> systemIds
                                     , final List<String> resolutionPaths) {
        this.systemIds       = systemIds;
        this.resolutionPaths = resolutionPaths;
    }

    @Override
    public void check(final List<ResourceResolutionCoordinates>    resourceResolutionCoordinates
               , final List<ResourceResolutionCoordinatesResolved> resourceResolutionCoordinatesResolved) {
        Assert.assertEquals(systemIds.size(), resourceResolutionCoordinates.size());
        for (int i = 0; i < systemIds.size(); i++) {
            Assert.assertEquals(String.format("problem in systemIds on index [%d]", i)
                                , systemIds.get(i)
                                , resourceResolutionCoordinates.get(i).systemId);
        }
        Assert.assertEquals(resolutionPaths.size(), resourceResolutionCoordinatesResolved.size());
        for (int i = 0; i < resolutionPaths.size(); i++) {
            Assert.assertEquals(String.format("problem in resolutionPaths on index [%d]", i)
                                , resolutionPaths.get(i)
                                , resourceResolutionCoordinatesResolved.get(i).path);
        }
    }
}
