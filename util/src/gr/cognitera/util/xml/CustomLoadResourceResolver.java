package gr.cognitera.util.xml;

import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import org.junit.Assert;


import gr.cognitera.util.base.StringUtil;

/**
 * Custom resource resolver that only deals with loading resources, not saving them.
 *
 */
public final class CustomLoadResourceResolver implements LSResourceResolver {

    private static final String EXCEPTION_MSG = String.format("The [%s] class implements only the 'Load' part of the %s interface"
                                                              , CustomLoadResourceResolver.class.getSimpleName()
                                                              , LSResourceResolver.class.getSimpleName());

    private final Logger              logger;
    private final boolean             loggerWasSupplanted;
    private final List<String>        resolvedResources;
    private final Class<?>            classForResourceResolution;
    private final Map<String, String> systemIdsToPaths;

    protected final List<ResourceResolutionCoordinates>         resourceResolutionCoordinates;         // used only during unit-testing
    protected final List<ResourceResolutionCoordinatesResolved> resourceResolutionCoordinatesResolved; // -----------------------------

    public CustomLoadResourceResolver(final Class<?> classForResourceResolution
                                      , final Map<String, String> systemIdsToPaths) {
        this((Logger) null, classForResourceResolution, systemIdsToPaths);
    }

    protected static boolean ALLOW_NULL_OR_EMPTY_SYSTEMIDS_TO_PATHS = false; // we only set this to true for unit-testing purposes
    public CustomLoadResourceResolver(final Logger _logger
                                      , final Class<?> classForResourceResolution
                                      , final Map<String, String> systemIdsToPaths) {
        // TODO: if no class is supplied, then all paths must be absolute
        if (_logger!=null) {
            this.logger = _logger;
            this.loggerWasSupplanted = true;
        } else {
            this.logger = Logger.getLogger(CustomLoadResourceResolver.class);
            this.loggerWasSupplanted = false;
        }
        this.resolvedResources = new ArrayList<String>();
        this.classForResourceResolution            = classForResourceResolution;
        this.systemIdsToPaths                      = systemIdsToPaths;
        this.resourceResolutionCoordinates         = new ArrayList<>();
        this.resourceResolutionCoordinatesResolved = new ArrayList<>();
        if (!ALLOW_NULL_OR_EMPTY_SYSTEMIDS_TO_PATHS) {
            Assert.assertNotNull(systemIdsToPaths);
            Assert.assertFalse  (systemIdsToPaths.isEmpty());
        }

    }

    private String logPreamble() {
        if (!loggerWasSupplanted)
            return "";
        else
            return String.format("(in class %s) "
                                 , CustomLoadResourceResolver.class.getName());
    }

    @Override
    public LSInput resolveResource(final String type
                                   , final String namespaceURI
                                   , final String publicId
                                   , final String systemId
                                   , final String baseURI) {

        logger.debug(String.format("%sresolveResource(..) * type is: [%s], namespaceURI is: [%s]"
                                   +", publicId is: [%s], systemId is: [%s], baseURI is: [%s]"
                                   , logPreamble(), type, namespaceURI, publicId, systemId, baseURI));
        final ResourceResolutionCoordinates resourceResolutionCoordinate = new ResourceResolutionCoordinates(type
                                                                                                             , namespaceURI
                                                                                                             , publicId
                                                                                                             , systemId
                                                                                                             , baseURI);
        resourceResolutionCoordinates.add(resourceResolutionCoordinate);
        return new LSInput() {
            @Override
            public String getBaseURI() {
                return null;
            }
                
            @Override
            public InputStream getByteStream() {
                /*
                 * Only resolve each include once. Otherwise there will be
                 * errors about duplicate definitions
                 */
                if (!resolvedResources.contains(systemId)) {
                    if ((systemIdsToPaths!=null) && systemIdsToPaths.containsKey(systemId)) {
                        final String path = systemIdsToPaths.get(systemId);
                        resolvedResources.add(systemId);
                        resourceResolutionCoordinatesResolved.add(new ResourceResolutionCoordinatesResolved(resourceResolutionCoordinate
                                                                                                            , path));
                        final InputStream is = classForResourceResolution.getResourceAsStream(path);
                        if (is==null)
                            throw new RuntimeException(String.format("resource at [%s] relative to class [%s] not found"
                                                                     , path
                                                                     , classForResourceResolution.getName()));
                        else
                            return is;
                    } else
                        return null;
                } else {
                    return null;
                }
            }

            @Override
            public boolean getCertifiedText() {
                return false;
            }

            @Override
            public Reader getCharacterStream() {
                return null;
            }

            @Override
            public String getEncoding() {
                return null;
            }

            @Override
            public String getPublicId() {
                return null;
            }

            @Override
            public String getStringData() {
                return null;
            }

            @Override
            public String getSystemId() {
                return null;
            }

            @Override
            public void setBaseURI(String baseURI) {
                throw new UnsupportedOperationException(EXCEPTION_MSG);
            }

            @Override
            public void setByteStream(InputStream byteStream) {
                throw new UnsupportedOperationException(EXCEPTION_MSG);
            }

            @Override
            public void setCertifiedText(boolean certifiedText) {
                throw new UnsupportedOperationException(EXCEPTION_MSG);
            }

            @Override
            public void setCharacterStream(Reader characterStream) {
                throw new UnsupportedOperationException(EXCEPTION_MSG);
            }

            @Override
            public void setEncoding(String encoding) {
                throw new UnsupportedOperationException(EXCEPTION_MSG);
            }

            @Override
            public void setPublicId(String publicId) {
                throw new UnsupportedOperationException(EXCEPTION_MSG);
            }

            @Override
            public void setStringData(String stringData) {
                throw new UnsupportedOperationException(EXCEPTION_MSG);
            }

            @Override
            public void setSystemId(String systemId) {
                throw new UnsupportedOperationException(EXCEPTION_MSG);
            }
        };
    }
}
