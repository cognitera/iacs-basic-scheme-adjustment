package gr.cognitera.util.ivoa;

import java.util.List;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

    
import gr.cognitera.util.xml.DOMUtils;

import gr.cognitera.util.chandra.ICellAccessor;


public class VOTable implements ICellAccessor {

    public List<VOTField> fields;
    public List<List<String>> tabledata;


    public VOTable(final List<VOTField> fields, final List<List<String>> tabledata) {
        this.fields    = fields;
        this.tabledata = tabledata;
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("fields"        , fields)
            .add("# of tabledata rows", tabledata.size())
            ;
    }
    @Override
    public String toString() {
        return toStringHelper().toString();
    }    
    

    @Override
    public int numberOfFields() {
        return fields.size();
    }

    @Override
    public int numberOfRows() {
        return tabledata.size();
    }

    @Override
    public List<String> row(int row) {
        if ((row < 0) || (row > tabledata.size())) {
            throw new IllegalArgumentException(String.format("Can't access row [%d] (total number of rows is: [%d])"
                                                             , row
                                                             , tabledata.size()));
        } else {
            return tabledata.get(row);
        }
    }

    public String getFieldsAndDataConsistencyProblem() {
        return getFieldsAndDataConsistencyProblem(this.fields, this.tabledata);
    }

    public static String getFieldsAndDataConsistencyProblem(List<VOTField> fields, List<List<String>> tabledata) {
        final int N = fields.size();
        for (int i = 0 ; i < tabledata.size() ; i++) {
            List<String> row = tabledata.get(i);
            if (row.size()!=N)
                return String.format("row [%d] has length [%d] whereas # of fields is: [%d]"
                                     , i, row.size(), N);
        }
        return (String) null;
    }

    public String fieldsHaveNameAttributeProblem() {
        final Integer idxOfFirstThatDoesntIncludeMandatoryNameAttr = VOTField.idxThatDoesntHaveNameAttr(this.fields);
        if (idxOfFirstThatDoesntIncludeMandatoryNameAttr==null)
            return null;
        else
            return String.format("The %s instance with idx=[%d] does not include the mandatory [name] attribute"
                                 , VOTField.class.getName()
                                 , idxOfFirstThatDoesntIncludeMandatoryNameAttr);
    }

    public String fieldsHaveNameOrIDAttributeProblem() {
        final Integer idx = VOTField.idxThatDoesntHaveNameOrIdAttr(this.fields);
        if (idx==null)
            return null;
        else
            return String.format("The %s instance with idx=[%d] does not include the mandatory [name] or [id] attribute"
                                 , VOTField.class.getName()
                                 , idx);
    }    


    public static VOTable parse(final Document doc) {
        DOMUtils.assertDocumentElementTag(doc, "VOTABLE");
        final List<VOTField> fields = VOTField.extractFieldsFromVOTable(doc);
        final NodeList TRs = doc.getElementsByTagName("TR");
        List<List<String>> tableData = new ArrayList<List<String>>(TRs.getLength());
        for (int i = 0 ; i < TRs.getLength() ; i++) {
            final NodeList TDs = TRs.item(i).getChildNodes();
            List<String> row = new ArrayList<String>(TDs.getLength());
            for (int j = 0 ; j < TDs.getLength() ; j++) {
                final Node node = TDs.item(j);
                if (node.getNodeName().equals("TD"))
                    row.add(node.getTextContent());
            }
            tableData.add(row);
        }
        return new VOTable(fields, tableData);
    }
}
