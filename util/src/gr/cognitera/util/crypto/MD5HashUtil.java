package gr.cognitera.util.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5HashUtil {
    
    public static String getMD5Hash(final byte[] blob) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("MD5");
        final byte[] hash = md.digest(blob);
        final StringBuilder sb = new StringBuilder(2*hash.length);
        for (final byte b : hash){
            sb.append(String.format("%02x", b&0xff));
        }
        return sb.toString();
    }
}
