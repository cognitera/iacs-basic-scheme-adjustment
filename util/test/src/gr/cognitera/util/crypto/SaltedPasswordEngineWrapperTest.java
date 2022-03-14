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

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.net.URL;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import org.junit.rules.ErrorCollector;
import org.junit.Rule;

import javax.xml.bind.DatatypeConverter;

import com.google.common.base.Joiner;

import gr.cognitera.util.base.RandomStringGenerator;


class SaltedPasswordEngineWrapperDerived extends SaltedPasswordEngineWrapper {
    @Override
    protected String hardcodedPepper() {
        return "pepper, in moderation, is good for you!";
    }
    
    @Override
    protected int hardcodedAdditionalIterations() {
        return 3;
    }
    
    public SaltedPasswordEngineWrapperDerived(Charset _saltCharset, int _keyLength) {
        super(_saltCharset, _keyLength);
    }
}

public class SaltedPasswordEngineWrapperTest {
    @Test
    public void displaySimpleHash() {
        SaltedPasswordEngineWrapperDerived saltUtil = new SaltedPasswordEngineWrapperDerived(StandardCharsets.UTF_8, 50*8);
        String password = "foo";
        String hash = saltUtil.hash(SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1, password, "some salt", 100000);
        System.out.printf("\n\n\n\n\t\t[%s]=>[%s]\n\n\n", password, hash);
    }

    
    @Test
    public void testDatatypeConverterWithRandomByteArrays() {
        RandomStringGenerator r = new RandomStringGenerator();
        SaltedPasswordEngineWrapper[] saltedUtils = {
              new SaltedPasswordEngineWrapper(StandardCharsets.UTF_8, 50*8)
              , new SaltedPasswordEngineWrapperDerived(StandardCharsets.UTF_8, 50*8)
        };
        for (int i = 0 ; i < 2 ; i++) {
            SaltedPasswordEngineWrapper saltedUtil      = saltedUtils[i];
            SaltedPasswordEngineWrapper saltedUtilOther = saltedUtils[(i+1)%saltedUtils.length];
            for (int j = 0 ; j < 1 ; j++) {
                String password = r.next(100);
                String salt     = r.next(30);
                int iterations = 100*1000;            
                String hash = saltedUtil.hash(SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1, password, salt, iterations);
                if (false)
                    System.out.printf("Password [%s], salt [%s] => [%s]\n"
                                      , password
                                      , salt
                                      , hash);
                assertTrue (hash.equals(saltedUtil     .hash(SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1, password,             salt             , iterations)));
                assertFalse(hash.equals(saltedUtilOther.hash(SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1, password,             salt             , iterations)));
                assertFalse(hash.equals(saltedUtil     .hash(SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1, password,             salt             , iterations+1)));
                assertFalse(hash.equals(saltedUtil     .hash(SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1, password,             salt.concat("a") , iterations)));
                assertFalse(hash.equals(saltedUtil     .hash(SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1, password.concat("a"), salt             , iterations)));
                assertFalse(hash.equals(saltedUtil     .hash(SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1, password            , salt.concat("00"), iterations)));
            }
        }
    }
}


