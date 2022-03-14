package gr.cognitera.util.crypto;


import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;

import java.util.Arrays;
    

// adapted from: http://stackoverflow.com/a/18143616/274677
/**
 * A utility class to hash passwords and check passwords vs hashed values. It uses a combination of hashing and unique
 * salt. The algorithm used to be PBKDF2WithHmacSHA1 which, although not the best for hashing password (vs. bcrypt) is
 * still considered robust and <a href="http://security.stackexchange.com/a/6415/12614"> recommended by NIST </a>.
 * The hashed value has 256 bits. Currently, however, PBKDF2WithHmacSHA512 is used which is a better cryptographic
 * hash and natively supported by Java 8
 */
public class SaltedPasswordEngine {

    /**
     * Returns a salted and hashed password using the provided hash.<br>
     * Note - side effect: the password is destroyed (the char[] is filled with zeros)
     *
     * @param algorithm   the algorithm to use
     * @param password    the password to be hashed
     * @param salt        a 16 bytes salt, ideally obtained with the getNextSalt method
     * @param iterations  iterations to hash
     * @param keyLength   length of key to produce
     *
     * @return the hashed password with a pinch of salt
     */
    public static byte[] hash(SecretKeyFactoryAlgorithm algorithm, char[] password, byte[] salt, int iterations, int keyLength) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm.getName());
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } finally {
            spec.clearPassword();
        }
    }

    /**
     * Returns true if the given password and salt match the hashed value, false otherwise.<br>
     * Note - side effect: the password is destroyed (the char[] is filled with zeros)
     *
     * @param algorithm    the algorithm to use
     * @param password     the password to check
     * @param salt         the salt used to hash the password
     * @param expectedHash the expected hashed value of the password
     * @param iterations   iterations to hash
     * @param keyLength    length of key to produce
     *
     * @return true if the given password and salt match the hashed value, false otherwise
     */
    public static boolean isExpectedPassword(SecretKeyFactoryAlgorithm algorithm, char[] password, byte[] salt, byte[] expectedHash, int iterations, int keyLength) {
        byte[] pwdHash = hash(algorithm, password, salt, iterations, keyLength);
        Arrays.fill(password, Character.MIN_VALUE);
        if (pwdHash.length != expectedHash.length) return false;
        for (int i = 0; i < pwdHash.length; i++) {
            if (pwdHash[i] != expectedHash[i]) return false;
        }
        return true;
    }


    

}
