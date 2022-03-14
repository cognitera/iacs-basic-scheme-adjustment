package gr.cognitera.util.crypto;

import java.util.Arrays;

import java.nio.charset.Charset;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;

import javax.xml.bind.DatatypeConverter; 
    

public class SaltedPasswordEngineWrapper {

    private Charset saltCharset;
    private int     keyLength;

    // subclass this class and override this method to define
    // an application-specific secret (so that part of the salt lives
    // in the code as well in accordance to best practices)
    protected String hardcodedPepper() {
        return "";
    }

    protected int hardcodedAdditionalIterations() {
        return 0;
    }

    public SaltedPasswordEngineWrapper(Charset _saltCharset, int _keyLength) {
        this.saltCharset = _saltCharset;
        this.keyLength   = _keyLength;
    }

    public String hash(SecretKeyFactoryAlgorithm secretKeyFactoryAlgorithm, String _password, String __salt, int _iterations) {
        char[] password = _password.toCharArray();
        String _salt    = __salt.concat(hardcodedPepper());
        byte[] salt     = _salt.getBytes(saltCharset);
        int iterations  = _iterations + hardcodedAdditionalIterations();
        byte[] hashedSaltedPassword = SaltedPasswordEngine.hash(secretKeyFactoryAlgorithm, password, salt, iterations, keyLength);
        return DatatypeConverter.printHexBinary(hashedSaltedPassword);
    }
}
