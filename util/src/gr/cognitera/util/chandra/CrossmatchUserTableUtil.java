package gr.cognitera.util.chandra;

import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

import org.junit.Assert;
import org.w3c.dom.Document;

import gr.cognitera.util.base.Util;
import gr.cognitera.util.base.StringUtil;
import gr.cognitera.util.base.SCAUtils;
import gr.cognitera.util.xml.DOMUtils;
import gr.cognitera.util.ivoa.VOTable;
import gr.cognitera.util.ivoa.VOTField;

import com.google.common.base.Throwables;
    

public class CrossmatchUserTableUtil {

    private CrossmatchUserTableUtil() {}

    public static CrossmatchUserTableConfig extractConfiguration(final String s, final boolean tolerateAbsenceOfNameIfIDIsProvided) {
        final CrossmatchUserTableType type = extractType(s, tolerateAbsenceOfNameIfIDIsProvided);
        List<String> columns;
        switch (type) {
        case VOT:
            final VOTable x = VOTable.parse(DOMUtils.parse(s));
            columns = VOTField.projectNameOrIfMissingThenId(x.fields);
            break;
        case TSV:
            final TSV y = TSV.parse(s);
            columns = y.fields;            
            break;
        default:
            throw new RuntimeException(String.format("unhandled type: %s"
                                                     , type));
        }
        return new CrossmatchUserTableConfig(type, columns);
    }

    private static boolean looksLikesHexagesimal_in_HMS_or_DMS_notation(final String s) {
        /* TODO: the present implementation is extremely loose and just barely manages
         *       to detect some flagrantly wrong test cases:
         *       - error-3.xml
         *       - error-2.tsv
         *       
         *       At some point, a more nuanced implementation should be provided. As of
         *       2019-10-23 I believe that we have such code in JS only.
         */
        final String forbiddenChars="abcdefghijklmnopqrstuvwxyz"
            +"_{}[]!@#$%^&*()_+"
            +"`={}|[]\\<>?,/";
        final int N = forbiddenChars.length();
        for (int i = 0; i < N; i++) {
            if (s.toLowerCase().contains(forbiddenChars.substring(i, i+1)))
                return false;
        }
        return true;
    }

    private static int number_of_candidate_RA_DEC_columns(final ICellAccessor x) {
        Set<Integer> likely_RA_DEC_columns = new LinkedHashSet<>();
        for (int i = 0 ; i < x.numberOfFields(); i++) {
            Assert.assertTrue(likely_RA_DEC_columns.add(i));
        }
        for (int r = 0; r < x.numberOfRows() ; r++) {
            List<String> row = x.row(r);
            int i = 0;
            for (final String cell: row) {
                final boolean is_in_sexagesimal_HMS_DMS_notation = true;
                if (StringUtil.isNumber(cell) || looksLikesHexagesimal_in_HMS_or_DMS_notation(cell))
                    ;
                else
                    likely_RA_DEC_columns.remove(i);
                i++;
            }
        }
        return likely_RA_DEC_columns.size();
    }

    private static String problemOnCandidateRA_and_DEC_columns(final ICellAccessor x) {
        final int n = number_of_candidate_RA_DEC_columns(x);
        if (n<2)
            return String.format("could not detect at least two (2) likely RA and DEC columns which every User Table for Crossmatch must, at a minimum, provide - only [%d] were detected"
                                 , n);
        else
            return null;
    }

    public static String isWellFormedProblem(final String s, final boolean tolerateAbsenceOfNameIfIDIsProvided) {
        Boolean isXML     = null;
        Boolean isVOTable = null;
        Boolean isTSV     = null;
        if (DOMUtils.isXML(s)) {
            try {
                final VOTable votable = VOTable.parse(DOMUtils.parse(s));
                final String problem = votable.getFieldsAndDataConsistencyProblem();
                if (problem != null)
                    return problem;
                else {
                    final String problem2 = tolerateAbsenceOfNameIfIDIsProvided
                        ? votable.fieldsHaveNameOrIDAttributeProblem()
                        : votable.fieldsHaveNameAttributeProblem();
                    if (problem2!=null)
                        return problem2;
                    else
                        return problemOnCandidateRA_and_DEC_columns(votable);
                }
            } catch (Throwable t) {
                return String.format("Is an XML file but doesn't appear to be a VOTable. Message was: [%s]"
                                     , t.getMessage());
            }
        } else {
            try {
                final TSV tsv = TSV.parse(s);
                return problemOnCandidateRA_and_DEC_columns(tsv);
            } catch (Throwable t) {
                return String.format("Is not an XML file, but neither is it TSV. Message was: [%s]"
                                     , t.getMessage());
            }
        }

    }

    public static CrossmatchUserTableType extractType(final String s, final boolean tolerateAbsenceOfNameIfIDIsProvided) {
        final String problem = isWellFormedProblem(s, tolerateAbsenceOfNameIfIDIsProvided);
        if (problem != null)
            throw new IllegalArgumentException(String.format("Problem while extracting configuration information from String [%s]: %s"
                                                             , s
                                                             , problem));
        else {
            Throwable aT = null;
            Throwable bT = null;
            try {
                VOTable.parse(DOMUtils.parse(s));  // called only to verify it can be parsed; return value ignored
                return CrossmatchUserTableType.VOT; /* It's technically possible for an XML file to be construed as a TSV file
                                                       (having only one field on each row and no tabs at all). This is explicitly
                                                       allowed in the definition in:

                                                           /stage/ascds_extra/staff/UDF/Databases/level3/UI/Design/TSV/tsv-output.txt

                                                       ... where we read (in the BNF productions):

                                                           "record ::= field [TAB field]+ EOL   # at least one field, or more"

                                                       ... but we're being pragmatic here.
                                                    */
            } catch (Throwable t) {
                aT = t;
                try {
                    TSV.parse(s); // called only to verify it can be parsed; return value ignored
                    return CrossmatchUserTableType.TSV;
                } catch (Throwable t2) {
                    bT = t2;
                }
            }

            Assert.assertNotNull(aT);
            Assert.assertNotNull(bT);
            final String MSG = String.format("aT=[%s], bT=[%s]"
                                             , Throwables.getStackTraceAsString(aT)
                                             , Throwables.getStackTraceAsString(bT));
            Assert.fail(MSG);
            return SCAUtils.CANT_REACH_THIS_LINE(CrossmatchUserTableType.class);
        }
    }
}
