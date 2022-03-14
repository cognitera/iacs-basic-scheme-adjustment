package gr.cognitera.util.crypto;

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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import org.junit.rules.ErrorCollector;
import org.junit.Rule;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import com.google.common.io.ByteStreams;
    

class ConstructorSupplier {
    private String fname;
    private String expectedMD5;

    public ConstructorSupplier(final String fname, final String expectedMD5) {
        this.fname       = fname;
        this.expectedMD5 = expectedMD5;
    }

    public Object[] supplyArguments() {
        try {
            File directory=new File("test/test-files/crypto");
            if (!directory.isDirectory())
                throw new RuntimeException(String.format("File [%s] is not a directory or does not exist!"
                                                         , directory.getPath()));
            File file = new File(directory, fname);
            if (!file.isFile())
                throw new RuntimeException(String.format("File [%s] is not a file or does not exist!"
                                                         , file.getPath()));
            final InputStream in = new FileInputStream(file);
            final byte[] blob = ByteStreams.toByteArray(in);
            in.close();
            return new Object[]{fname, blob, expectedMD5};
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}


@RunWith(Parameterized.class)
public class MD5HashUtilTest {

    private final String fname;
    private final byte[] blob;
    private final String expectedMD5;


    public MD5HashUtilTest(final String fname,
                           final byte[] blob,
                           final String expectedMD5) {
        this.fname = fname;
        this.blob = blob;
        this.expectedMD5 = expectedMD5;
    }

    private static List<ConstructorSupplier> _data() {
        List<ConstructorSupplier> rv = new ArrayList<>();
        rv.add(new ConstructorSupplier("empty", "d41d8cd98f00b204e9800998ecf8427e"));
        rv.add(new ConstructorSupplier("poem" , "9119ddd9c904998631c0d21c9c0c8798"));
        return rv;
    }

    @Parameters(name = "{index}: {0} ({1})}")
    public static Collection<Object[]> data() {
        List<Object[]> rv = new ArrayList<>();        
        for (ConstructorSupplier x: _data())
            rv.add(x.supplyArguments());
        return rv;
    }
    
    @Test
    public void testMD5AsExpected() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        final String actualMD5 = MD5HashUtil.getMD5Hash(blob);
        Assert.assertEquals(expectedMD5, actualMD5);
    }
}



