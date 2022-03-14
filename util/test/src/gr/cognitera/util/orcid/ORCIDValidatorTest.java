package gr.cognitera.util.orcid;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;

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

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.net.URL;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import org.junit.rules.ErrorCollector;
import org.junit.Rule;

import javax.xml.bind.DatatypeConverter;

import com.google.common.base.Joiner;


public class ORCIDValidatorTest {

    @Test
    public void demonstrateThatClassesAreVisible() {
        ORCIDValidator         x1 = null;
        ORCIDValidationFailure x2 = null;        
    }


    @Test
    public void malformednessTests() {
        String wellFormedORCIDs[] = new String[]{
            "0000-0000-0000-0000",
            "0000-0000-0000-000X",
            "9999-9999-9999-9999"
        };
        String malformedORCIDs[] = new String[]{
            "0000-0000-0000-00000",
            "0000-0000-0000-000",
            "0000-0000-0000-0000-0",
            "000-0000-0000-0000",
            "000X-0000-0000-0000",
            "0000-000X-0000-0000",
            "0000-0000-000X-0000",
            "0000-00000000-0000",
            "",
            "foo",
            "some random shit"
        };
        for (String wellFormedORCID: wellFormedORCIDs)
            Assert.assertTrue(ORCIDValidator.isWellFormed(wellFormedORCID));
        for (String malformedORCID: malformedORCIDs)
            Assert.assertFalse(ORCIDValidator.isWellFormed(malformedORCID));        
    }

    @Test
    public void checkdigitTests() {
        List<OrcidAndChecksum> orcidsAndChecksums = OrcidAndChecksum.create(
                                                                            "0000-0003-1415-9269", "9",
                                                                            "0000-0002-1825-0097", "7"
                                                                            );
        for (OrcidAndChecksum orcidsAndChecksum: orcidsAndChecksums) {
            Assert.assertEquals(ORCIDValidator.calculateCheckDigitOnFullHypenatedORCID(orcidsAndChecksum.orcid)
                                , orcidsAndChecksum.checksum);
        }
    }
}

class OrcidAndChecksum {

    public String orcid;
    public String checksum;
    
    public OrcidAndChecksum(final String orcid, final String checksum) {
        this.orcid    = orcid;
        this.checksum = checksum;
    }

    public static List<OrcidAndChecksum> create(String ...xs) {
        Assert.assertTrue(xs.length % 2 == 0);
        List<OrcidAndChecksum> rv = new ArrayList<>();
        for (int i = 0; i < xs.length / 2; i++) {
            rv.add(new OrcidAndChecksum(xs[2*i], xs[2*i+1]));
        }
        return rv;
    }
}
