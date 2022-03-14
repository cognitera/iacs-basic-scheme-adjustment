package gr.cognitera.util.chandra;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.everyItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

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

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

import org.junit.rules.ErrorCollector;
import org.junit.Rule;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;


class ConstructorSupplier {

    public static final String GOOD = "good-";
    public static final String ERROR= "error-";
    public static final String MARGINAL = "marginal-";
    private static final String XML=".xml";
    private static final String TSV=".tsv";
    
    private final String fname;
    private final boolean marginal;
    private final List<String> columnNames;


    public ConstructorSupplier(final String fname, final List<String> columnNames) {
        Assert.assertTrue(fname.startsWith(GOOD) || fname.startsWith(ERROR) || fname.startsWith(MARGINAL));
        Assert.assertTrue((fname.startsWith(GOOD) && (columnNames!=null) && (columnNames.size()>=1))
                          ||
                          (fname.startsWith(ERROR) && (columnNames==null))
                          ||
                          (fname.startsWith(MARGINAL) && (columnNames!=null) && (columnNames.size()>=1))
                          );
        Assert.assertTrue(fname.endsWith(XML) || fname.endsWith(TSV));
        this.fname          = fname;
        this.columnNames    = columnNames;
        this.marginal       = fname.startsWith(MARGINAL);
    }

    private static CrossmatchUserTableType typeFromFilename(final String fname) {
        if (fname.endsWith(XML))
            return CrossmatchUserTableType.VOT;
        else if (fname.endsWith(TSV))
            return CrossmatchUserTableType.TSV;
        else throw new RuntimeException(String.format("unrecognized filename: [%s]"
                                                      , fname));
    }

    private static CrossmatchUserTableConfig configurationFromFilenameAndColumnNames(final String fname, final List<String> columnNames) {
        if (fname.startsWith(ERROR))
            return null;
        else // GOOD and MARGINAL:
            return new CrossmatchUserTableConfig(typeFromFilename(fname), columnNames);
    }

    public Object[] supplyArguments() {
        try {
            File directory=new File("test/test-files/csc-crossmatch-user-tables");
            if (!directory.isDirectory())
                throw new RuntimeException(String.format("File [%s] is not a directory or does not exist!"
                                                         , directory.getPath()));
            File file = new File(directory, fname);
            if (!file.isFile())
                throw new RuntimeException(String.format("File [%s] is not a file or does not exist!"
                                                         , file.getPath()));
            final String s = Files.toString(file, StandardCharsets.UTF_8);
            return new Object[]{fname, s, marginal, configurationFromFilenameAndColumnNames(fname, columnNames)};
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}


@RunWith(Parameterized.class)
public class CrossmatchUserTableUtilTest {

    private final String                    fname;
    private final String                    s;
    private final boolean                   marginal;
    private final CrossmatchUserTableConfig expectedConfiguration;


    public CrossmatchUserTableUtilTest(final String                    fname,
                                       final String                    s,
                                       final boolean                   marginal,
                                       final CrossmatchUserTableConfig expectedConfiguration) {
        this.fname                 = fname;
        this.s                     = s;
        this.marginal              = marginal;
        this.expectedConfiguration = expectedConfiguration;
    }

    private static List<ConstructorSupplier> _data() {
        List<ConstructorSupplier> rv = new ArrayList<>();
        rv.add(new ConstructorSupplier("good-1.xml", Arrays.asList(new String[]{"name", "ra", "dec", "err_ellipse_r0"})));
        rv.add(new ConstructorSupplier("good-1.tsv", Arrays.asList(new String[]{"name", "ra", "dec", "err_ellipse_r0"})));
        rv.add(new ConstructorSupplier("good-2.tsv", Arrays.asList(new String[]{"a", "b", "c"})));
        rv.add(new ConstructorSupplier("good-3.tsv", Arrays.asList(new String[]{"a", "b", "c"})));
        rv.add(new ConstructorSupplier("good-ChaserOutput1.tsv", Arrays.asList(new String[]{
                        "Seq Num",
                        "Obs ID",
                        "Instrument",
                        "Grating",
                        "Appr Exp ", // the TSV file supplied by David van Stone does indeed include a trailing space at three column' names'
                        "Exposure ", // as above
                        "Target Name",
                        "PI Name",
                        "RA",
                        "Dec",
                        "Status",
                        "Data Mode",
                        "Exp Mode ", // as above
                        "Avg Cnt Rate",
                        "Evt Cnt",
                        "Start Date",
                        "Public Release Date",
                        "Proposal",
                        "Science Category",
                        "Type",
                        "Obs Cycle",
                        "Prop Cycle",
                        "Joint",
                        "Grid Name"})));
        rv.add(new ConstructorSupplier("error-ChaserOutput1.tsv", null));
        
        rv.add(new ConstructorSupplier("good-xmm-topcat.xml", Arrays.asList(new String[]{"DETID", "SRCID", "IAUNAME", "SRC_NUM", "MATCH_1XMM", "SEP_1XMM", "SRCID_2XMMP", "MATCH_2XMMP", "SEP_2XMMP", "OBSID", "REVOLUT", "MJD_START", "MJD_STOP", "OBS_CLASS", "PN_FILTER", "M1_FILTER", "M2_FILTER", "PN_SUBMODE", "M1_SUBMODE", "M2_SUBMODE", "RA", "DEC", "POSERR", "LII", "BII", "RADEC_ERR", "SYSERR", "RA_UNC", "DEC_UNC", "CX", "CY", "CZ", "HTMID", "EP_1_FLUX", "EP_1_FLUX_ERR", "EP_2_FLUX", "EP_2_FLUX_ERR", "EP_3_FLUX", "EP_3_FLUX_ERR", "EP_4_FLUX", "EP_4_FLUX_ERR", "EP_5_FLUX", "EP_5_FLUX_ERR", "EP_8_FLUX", "EP_8_FLUX_ERR", "EP_9_FLUX", "EP_9_FLUX_ERR", "PN_1_FLUX", "PN_1_FLUX_ERR", "PN_2_FLUX", "PN_2_FLUX_ERR", "PN_3_FLUX", "PN_3_FLUX_ERR", "PN_4_FLUX", "PN_4_FLUX_ERR", "PN_5_FLUX", "PN_5_FLUX_ERR", "PN_8_FLUX", "PN_8_FLUX_ERR", "PN_9_FLUX", "PN_9_FLUX_ERR", "M1_1_FLUX", "M1_1_FLUX_ERR", "M1_2_FLUX", "M1_2_FLUX_ERR", "M1_3_FLUX", "M1_3_FLUX_ERR", "M1_4_FLUX", "M1_4_FLUX_ERR", "M1_5_FLUX", "M1_5_FLUX_ERR", "M1_8_FLUX", "M1_8_FLUX_ERR", "M1_9_FLUX", "M1_9_FLUX_ERR", "M2_1_FLUX", "M2_1_FLUX_ERR", "M2_2_FLUX", "M2_2_FLUX_ERR", "M2_3_FLUX", "M2_3_FLUX_ERR", "M2_4_FLUX", "M2_4_FLUX_ERR", "M2_5_FLUX", "M2_5_FLUX_ERR", "M2_8_FLUX", "M2_8_FLUX_ERR", "M2_9_FLUX", "M2_9_FLUX_ERR", "EP_8_RATE", "EP_8_RATE_ERR", "EP_9_RATE", "EP_9_RATE_ERR", "PN_1_RATE", "PN_1_RATE_ERR", "PN_2_RATE", "PN_2_RATE_ERR", "PN_3_RATE", "PN_3_RATE_ERR", "PN_4_RATE", "PN_4_RATE_ERR", "PN_5_RATE", "PN_5_RATE_ERR", "PN_8_RATE", "PN_8_RATE_ERR", "PN_9_RATE", "PN_9_RATE_ERR", "M1_1_RATE", "M1_1_RATE_ERR", "M1_2_RATE", "M1_2_RATE_ERR", "M1_3_RATE", "M1_3_RATE_ERR", "M1_4_RATE", "M1_4_RATE_ERR", "M1_5_RATE", "M1_5_RATE_ERR", "M1_8_RATE", "M1_8_RATE_ERR", "M1_9_RATE", "M1_9_RATE_ERR", "M2_1_RATE", "M2_1_RATE_ERR", "M2_2_RATE", "M2_2_RATE_ERR", "M2_3_RATE", "M2_3_RATE_ERR", "M2_4_RATE", "M2_4_RATE_ERR", "M2_5_RATE", "M2_5_RATE_ERR", "M2_8_RATE", "M2_8_RATE_ERR", "M2_9_RATE", "M2_9_RATE_ERR", "EP_8_CTS", "EP_8_CTS_ERR", "PN_8_CTS", "PN_8_CTS_ERR", "M1_8_CTS", "M1_8_CTS_ERR", "M2_8_CTS", "M2_8_CTS_ERR", "EP_8_DET_ML", "EP_9_DET_ML", "PN_1_DET_ML", "PN_2_DET_ML", "PN_3_DET_ML", "PN_4_DET_ML", "PN_5_DET_ML", "PN_8_DET_ML", "PN_9_DET_ML", "M1_1_DET_ML", "M1_2_DET_ML", "M1_3_DET_ML", "M1_4_DET_ML", "M1_5_DET_ML", "M1_8_DET_ML", "M1_9_DET_ML", "M2_1_DET_ML", "M2_2_DET_ML", "M2_3_DET_ML", "M2_4_DET_ML", "M2_5_DET_ML", "M2_8_DET_ML", "M2_9_DET_ML", "EP_EXTENT", "EP_EXTENT_ERR", "EP_EXTENT_ML", "EP_HR1", "EP_HR1_ERR", "EP_HR2", "EP_HR2_ERR", "EP_HR3", "EP_HR3_ERR", "EP_HR4", "EP_HR4_ERR", "PN_HR1", "PN_HR1_ERR", "PN_HR2", "PN_HR2_ERR", "PN_HR3", "PN_HR3_ERR", "PN_HR4", "PN_HR4_ERR", "M1_HR1", "M1_HR1_ERR", "M1_HR2", "M1_HR2_ERR", "M1_HR3", "M1_HR3_ERR", "M1_HR4", "M1_HR4_ERR", "M2_HR1", "M2_HR1_ERR", "M2_HR2", "M2_HR2_ERR", "M2_HR3", "M2_HR3_ERR", "M2_HR4", "M2_HR4_ERR", "PN_1_EXP", "PN_2_EXP", "PN_3_EXP", "PN_4_EXP", "PN_5_EXP", "M1_1_EXP", "M1_2_EXP", "M1_3_EXP", "M1_4_EXP", "M1_5_EXP", "M2_1_EXP", "M2_2_EXP", "M2_3_EXP", "M2_4_EXP", "M2_5_EXP", "PN_1_BG", "PN_2_BG", "PN_3_BG", "PN_4_BG", "PN_5_BG", "M1_1_BG", "M1_2_BG", "M1_3_BG", "M1_4_BG", "M1_5_BG", "M2_1_BG", "M2_2_BG", "M2_3_BG", "M2_4_BG", "M2_5_BG", "PN_1_VIG", "PN_2_VIG", "PN_3_VIG", "PN_4_VIG", "PN_5_VIG", "M1_1_VIG", "M1_2_VIG", "M1_3_VIG", "M1_4_VIG", "M1_5_VIG", "M2_1_VIG", "M2_2_VIG", "M2_3_VIG", "M2_4_VIG", "M2_5_VIG", "PN_ONTIME", "M1_ONTIME", "M2_ONTIME", "PN_OFFAX", "M1_OFFAX", "M2_OFFAX", "PN_MASKFRAC", "M1_MASKFRAC", "M2_MASKFRAC", "DIST_NN", "SUM_FLAG", "EP_FLAG", "PN_FLAG", "M1_FLAG", "M2_FLAG", "TSERIES", "SPECTRA", "EP_CHI2PROB", "PN_CHI2PROB", "M1_CHI2PROB", "M2_CHI2PROB", "VAR_FLAG", "VAR_EXP_ID", "VAR_INST_ID", "SC_RA", "SC_DEC", "SC_POSERR", "SC_EP_1_FLUX", "SC_EP_1_FLUX_ERR", "SC_EP_2_FLUX", "SC_EP_2_FLUX_ERR", "SC_EP_3_FLUX", "SC_EP_3_FLUX_ERR", "SC_EP_4_FLUX", "SC_EP_4_FLUX_ERR", "SC_EP_5_FLUX", "SC_EP_5_FLUX_ERR", "SC_EP_8_FLUX", "SC_EP_8_FLUX_ERR", "SC_EP_9_FLUX", "SC_EP_9_FLUX_ERR", "SC_HR1", "SC_HR1_ERR", "SC_HR2", "SC_HR2_ERR", "SC_HR3", "SC_HR3_ERR", "SC_HR4", "SC_HR4_ERR", "SC_DET_ML", "SC_EXT_ML", "SC_CHI2PROB", "SC_VAR_FLAG", "SC_SUM_FLAG", "N_DETECTIONS"})));
        rv.add(new ConstructorSupplier("good-ian-topcat.xml", Arrays.asList(new String[]{"_RAJ2000", "_DEJ2000", "recno", "B3", "RA1950", "DE1950", "Flux", "Fit", "ACOD1", "n_Diam", "Diam", "_RA.icrs", "_DE.icrs"})));
        rv.add(new ConstructorSupplier("good-cscview-cscresults-106586rows.xml", Arrays.asList(new String[]{"dataset_id", "name", "ra", "dec", "err_ellipse_r0", "conf_flag", "sat_src_flag", "significance", "flux_aper_b", "flux_aper_lolim_b", "flux_aper_hilim_b", "flux_aper_w", "flux_aper_lolim_w", "flux_aper_hilim_w"})));

        rv.add(new ConstructorSupplier("good-entire-catalog-v1.1-on-2019-10-18.tsv", Arrays.asList(new String[]{"dataset_id", "name", "ra", "dec", "err_ellipse_r0", "conf_flag", "sat_src_flag", "significance", "flux_aper_b", "flux_aper_lolim_b", "flux_aper_hilim_b", "flux_aper_w", "flux_aper_lolim_w", "flux_aper_hilim_w"})));
        rv.add(new ConstructorSupplier("good-entire-catalog-v1.1-on-2019-10-18-swollen-to-250K.xml", Arrays.asList(new String[]{"dataset_id", "name", "ra", "dec", "err_ellipse_r0", "conf_flag", "sat_src_flag", "significance", "flux_aper_b", "flux_aper_lolim_b", "flux_aper_hilim_b", "flux_aper_w", "flux_aper_lolim_w", "flux_aper_hilim_w"})));
        rv.add(new ConstructorSupplier("error-1.xml", null));
        rv.add(new ConstructorSupplier("error-2.xml", null));
        rv.add(new ConstructorSupplier("error-3.xml", null));
        rv.add(new ConstructorSupplier("error-1.tsv", null));
        rv.add(new ConstructorSupplier("error-2.tsv", null));
        rv.add(new ConstructorSupplier("marginal-NXSA-Results.xml", Arrays.asList(new String[]{"OBSERVATION_ID", "TARGET", "RA_NOM", "DEC_NOM", "REVOLUTION", "START_UTC", "END_UTC", "DURATION", "DESCRIPTION", "PI_SURNAME", "TYPE", "PROPRIETARY_END_DATE", "COORD_OBS", "OBSERVATION.OBSERVATION_DISTANCE_EQUATORIAL"})));
        return rv;
    }

    @Parameters(name = "{index}: {0}}")
    public static Collection<Object[]> data() {
        List<Object[]> rv = new ArrayList<>();        
        for (ConstructorSupplier x: _data())
            rv.add(x.supplyArguments());
        return rv;
    }
    
    @Test
    public void testConfigurationIsAsExpected() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        final String wellFormedProblemStrict   = CrossmatchUserTableUtil.isWellFormedProblem(s, false);
        final String wellFormedProblemTolerant = CrossmatchUserTableUtil.isWellFormedProblem(s,  true);
        if (expectedConfiguration!=null) {
            Assert.assertTrue(fname.startsWith(ConstructorSupplier.GOOD) || fname.startsWith(ConstructorSupplier.MARGINAL));
            // System.out.printf("wellFormedProblem = [%s]\n", wellFormedProblem);
            Assert.assertNull(wellFormedProblemTolerant);
            if (!marginal)
                Assert.assertNull(wellFormedProblemStrict);

            final CrossmatchUserTableConfig actualConfiguration = CrossmatchUserTableUtil.extractConfiguration(s, true);            
            // System.out.printf("configuration for file [%s] is: %s\n", fname, actualConfiguration);
            Assert.assertEquals(expectedConfiguration, actualConfiguration);
            if (!marginal) {
                final CrossmatchUserTableConfig actualConfigurationStrict = CrossmatchUserTableUtil.extractConfiguration(s, false);
                Assert.assertEquals(actualConfiguration, actualConfigurationStrict);
            }
        } else { // expected error case
            final String PREAMBLE = String.format("problem with [%s]", fname);
            Assert.assertTrue   (PREAMBLE, fname.startsWith(ConstructorSupplier.ERROR));
            Assert.assertNotNull(PREAMBLE, wellFormedProblemStrict);
            Assert.assertNotNull(PREAMBLE, wellFormedProblemTolerant);
        }
    }
}



