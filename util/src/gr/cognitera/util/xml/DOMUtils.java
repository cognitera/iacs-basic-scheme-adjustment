package gr.cognitera.util.xml;

import java.io.StringReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
    
import org.junit.Assert;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class DOMUtils {

    private DOMUtils() {}

    public static Document parse(final String s) {
        try {
            return _parse(s);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private static Document _parse(final String s) throws IOException, ParserConfigurationException, SAXException {
        final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(s));
        return db.parse(is);
    }

    public static Exception parseException(final String s) {
        try {
            parseForSideEffectExceptionsOnly(s);
            return null;
        } catch (IOException | ParserConfigurationException | SAXException e) {
            return e;
        }
    }

    @SuppressFBWarnings(value="DLS_DEAD_LOCAL_STORE"
                        , justification="parsed only for the side effect of potentially throwing an Exception;"
                                       +" still doesn't hurt if we strongly"
                                       +" type the expected return type")
    private static void parseForSideEffectExceptionsOnly(final String s) throws IOException
                                                                                , ParserConfigurationException
                                                                                , SAXException {
        final Document ignored = _parse(s);
    }

    
    public static boolean isXML(final String s) {
        try {
            parseForSideEffectExceptionsOnly(s);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    public static void assertNodeTag(final Node node, final String tag) {
        final String tagActual = node.getNodeName();
        Assert.assertEquals(String.format("Node has tag [%s] and not [%s] as expected"
                                          , tagActual
                                          , tag)
                            , tag
                            , tagActual);
    }

    public static void assertDocumentElementTag(final Document doc, final String tag) {
        final String tagActual = doc.getDocumentElement().getTagName();
        Assert.assertEquals(String.format("Document has document element with tag [%s] and not [%s] as expected"
                                          , tagActual
                                          , tag)
                            , tag
                            , tagActual);
    }
}
