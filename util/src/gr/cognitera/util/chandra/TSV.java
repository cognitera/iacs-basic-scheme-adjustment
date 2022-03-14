package gr.cognitera.util.chandra;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;


public class TSV implements ICellAccessor {

    public List<String> fields;
    public List<List<String>> rows;

    /**
     * Models a TSV file according to the specifications found in:
     *
     *     /stage/ascds_extra/staff/UDF/Databases/level3/UI/Design/TSV/tsv-output.txt
     *
     * For the time being we are ignoring column comments as the only intended use case at
     * the time of this writing is user-supplied tables for crossmatch queries against CSCCLI
     * where the column comments don't come into play.
     *
     */
    public TSV(final List<String> fields, final List<List<String>> rows) {
        Assert.assertNotNull(fields);
        Assert.assertNotNull(rows);
        Assert.assertTrue(fields.size()>=1); // in the TSV definition give above we read: "at least one field, or more"
        for (List<String> row: rows) {
            Assert.assertEquals(fields.size(), row.size());
        }
        this.fields = fields;
        this.rows = rows;
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("fields"        , Joiner.on(", ").join(fields))
            .add("# of data rows", rows.size())
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
        return rows.size();
    }

    @Override
    public List<String> row(int row) {
        if ((row < 0) || (row > rows.size())) {
            throw new IllegalArgumentException(String.format("Can't access row [%d] (total number of rows is: [%d])"
                                                             , row
                                                             , rows.size()));
        } else {
            return rows.get(row);
        }
    }    

    public static TSV parse(final String s) {
        final String[] lines = s.split("\\r?\\n");
        boolean inCommentsSection = true;
        boolean encounteredFirstNonCommentLine = false;
        int i = 0;
        List<String> fields = null;
        final List<List<String>> rows = new ArrayList<>();
        for (final String line: lines) {
            i++;
            if (line.startsWith("#")) {
                if (inCommentsSection)
                    continue;
                else
                    throw new IllegalArgumentException(String.format("unexpected comment on line #%d: [%s]"
                                                                      , i
                                                                      , line));
            } else {
                inCommentsSection = false;
                if (!encounteredFirstNonCommentLine) {
                    encounteredFirstNonCommentLine = true;
                    fields = tsvLineToList(line);
                } else {
                    Assert.assertNotNull(fields);
                    final List<String> row = tsvLineToList(line);
                    if (row.size()!=fields.size())
                        throw new IllegalArgumentException(String.format("line #%d: [%s] has %d fields whereas the number of fields has been established to %d"
                                                                         , i
                                                                         , line
                                                                         , row.size()
                                                                         , fields.size()));
                    rows.add(row);
                }
            }
        }
        return new TSV(fields, rows);
    }

    private static List<String> tsvLineToList(final String line) {
        return Arrays.asList(line.split("\\t", -1)); // https://stackoverflow.com/a/58648034/274677
    }
    
}
