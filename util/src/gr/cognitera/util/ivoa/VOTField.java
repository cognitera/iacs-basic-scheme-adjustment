package gr.cognitera.util.ivoa;

import java.util.List;
import java.util.ArrayList;

import org.junit.Assert;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;

import gr.cognitera.util.xml.DOMUtils;
import gr.cognitera.util.base.SCAUtils;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

enum FieldAttributeConfiguration {
    NAME_ONLY, ID_ONLY, NAME_AND_ID, BOTH_NAME_AND_ID_MISSING_FOR_SOME
}


public class VOTField {


    public final String id;
    public final String datatype;
    public final String name;
    public final String precision;
    public final String ucd;
    public final String unit;
    public final String width;
    public final String description;

    public VOTField(final String id,
                    final String datatype,
                    final String name,
                    final String precision,
                    final String ucd,
                    final String unit,
                    final String width,
                    final String description) {                    
        this.id          = id;
        this.datatype    = datatype;
        this.name        = name;
        this.precision   = precision;
        this.ucd         = ucd;
        this.unit        = unit;
        this.width       = width;
        this.description = description;
    }


    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("datatype", datatype)
            .add("name", name)
            .add("precision", precision)
            .add("ucd", ucd)
            .add("unit", unit)
            .add("width", width)
            .add("description", description)
            ;
    }
    @Override
    public String toString() {
        return toStringHelper().toString();
    }    
    

    public static List<VOTField> extractFieldsFromVOTable(final Document doc) {
        DOMUtils.assertDocumentElementTag(doc, "VOTABLE");
        final NodeList nodeList = doc.getElementsByTagName("FIELD");
        List<VOTField> rv = new ArrayList<>(nodeList.getLength());
        for (int i = 0 ; i < nodeList.getLength() ; i++) {
            final Node node = nodeList.item(i);
            /*<FIELD ID="col3" datatype="double" name="dec" precision="F5" ref="ICRScoords" ucd="pos.eq.dec;meta.main" utype="stc:AstroCoords.Position2D.Value2.C2" width="9">
                 <DESCRIPTION>Source position, ICRS declination</DESCRIPTION>
              </FIELD>
            */
            final String ID          = getAttribute  (node, "ID");
            final String datatype    = getAttribute  (node, "datatype");
            final String name        = getAttribute  (node, "name");
            final String precision   = getAttribute  (node, "precision");
            final String ucd         = getAttribute  (node, "ucd");
            final String unit        = getAttribute  (node, "unit");
            final String width       = getAttribute  (node, "width");
            final String description = getDescription(node);
            rv.add(new VOTField(ID, datatype, name, precision, ucd, unit, width, description));
        }
        return rv;
    }

    private static String getAttribute(final Node node, final String attrS) {
        final Attr attr = (Attr) (node.getAttributes().getNamedItem(attrS));
        if (attr != null)
            return attr.getValue();
        else
            return null;
    }

    private static String getDescription(final Node node) {
        DOMUtils.assertNodeTag(node, "FIELD");
        Node descriptionNode = null;
        final NodeList children = node.getChildNodes();
        for (int i = 0 ; i < children.getLength() ; i++) {
            if (children.item(i).getNodeName().equals("DESCRIPTION"))
                if (descriptionNode==null)
                    descriptionNode = children.item(i);
                else
                    Assert.fail("multiple description nodes found");
        }
        if (descriptionNode==null)
            return null;
        else
            return descriptionNode.getTextContent();
    }

    public static List<String> projectNameOrIfMissingThenId(final List<VOTField> fields) {
        Boolean useName= null;
        final FieldAttributeConfiguration configuration = detectConfiguration(fields);
        switch (configuration) {
        case NAME_ONLY:
        case NAME_AND_ID:
            useName = true;
            break;
        case ID_ONLY:
            useName = false;
            break;
        case BOTH_NAME_AND_ID_MISSING_FOR_SOME:
            throw new IllegalStateException("You are not supposed to call this method until "+
                                            "you've satisfied yourself that either the [name] "+
                                            "or the [id] attributes are consistently present. "+
                                            "The way to do that is to call the [idxThatDoesntHaveNameOrIdAttr] "+
                                            "method and confirm that it returns null.");
        }
        Assert.assertNotNull(useName);
        final List<String> rv = new ArrayList<>();
        for (final VOTField field: fields)
            if (useName)
                rv.add(field.name);
            else
                rv.add(field.id);
        return rv;
    }

    public static Integer idxThatDoesntHaveNameAttr(final List<VOTField> xs) {
        for (int i = 0; i < xs.size() ; i++) {
            if (xs.get(i).name==null)
                return i;
        }
        return null;
    }


    private static FieldAttributeConfiguration detectConfiguration (final List<VOTField> xs) {
        boolean nameAlwaysPresent = true;
        boolean idAlwaysPresent = true;
        for (VOTField x: xs) {
            if (x.name == null) nameAlwaysPresent = false;
            if (x.id   == null) idAlwaysPresent = false;
        }
        if (!nameAlwaysPresent && !idAlwaysPresent) return FieldAttributeConfiguration.BOTH_NAME_AND_ID_MISSING_FOR_SOME;
        if (!nameAlwaysPresent &&  idAlwaysPresent) return FieldAttributeConfiguration.ID_ONLY;
        if ( nameAlwaysPresent && !idAlwaysPresent) return FieldAttributeConfiguration.NAME_ONLY;
        if ( nameAlwaysPresent &&  idAlwaysPresent) return FieldAttributeConfiguration.NAME_AND_ID;
        return SCAUtils.CANT_REACH_THIS_LINE(FieldAttributeConfiguration.class);
    }


    public static Integer idxThatDoesntHaveNameOrIdAttr(final List<VOTField> xs) {
        boolean nameIsMissingAtLeastOnce = false;
        boolean idIsMissingAtLeastOnce   = false;
        for (int i = 0; i < xs.size() ; i++) {
            if ((xs.get(i).name   == null) && (xs.get(i).id   == null)) return i;
            if ((nameIsMissingAtLeastOnce) && (xs.get(i).id   == null)) return i;
            if ((idIsMissingAtLeastOnce)   && (xs.get(i).name == null)) return i;
            if (xs.get(i).name   == null) nameIsMissingAtLeastOnce = true;
            if (xs.get(i).id     == null) idIsMissingAtLeastOnce   = true;                
        }
        return null;
    }    
}


