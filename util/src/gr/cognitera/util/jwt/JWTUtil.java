package gr.cognitera.util.jwt;
    

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Base64;
import java.io.IOException;
import java.io.File;
import java.nio.charset.StandardCharsets;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import java.security.Key;


import com.google.gson.Gson;

final class AlgorithmAndBase64EncodedSecretKey {

    public final String algorithm;
    public final String encodedKey;

    protected AlgorithmAndBase64EncodedSecretKey(final String algorithm
                                                 , final String encodedKey) {
        this.algorithm = algorithm;
        this.encodedKey = encodedKey;
    }

}


public final class JWTUtil {

    private JWTUtil() {
    }


    public static String generateAlgoAndSecretKeyJSON() {
        final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        System.out.printf("Key created; algorithm = [%s], format = [%s]\n"
                          , key.getAlgorithm()
                          , key.getFormat());
        final String rv = secretKeyToString(key);
        final SecretKey key2 = stringToSecretKey(rv);
        Assert.assertEquals(key, key2);
        return rv;
    }

    public static final String secretKeyToString(final SecretKey secretKey) {
        final String algorithm = secretKey.getAlgorithm();
        final Base64.Encoder encoder = Base64.getEncoder();
        final String encodedKey = encoder.encodeToString(secretKey.getEncoded());
        final AlgorithmAndBase64EncodedSecretKey algoAndEncodedString = new AlgorithmAndBase64EncodedSecretKey(algorithm
                                                                                                               , encodedKey);
        final String json = (new Gson()).toJson(algoAndEncodedString);
        return encoder.encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    public static final SecretKey stringToSecretKey(final String algorithmAndEncodedKeyS) {
        final Base64.Decoder decoder = Base64.getDecoder();
        final String algorithmAndEncodedKeyJson = new String(decoder.decode(algorithmAndEncodedKeyS)
                                                             , StandardCharsets.UTF_8);
        final AlgorithmAndBase64EncodedSecretKey jsonObj = (new Gson()).fromJson(algorithmAndEncodedKeyJson
                                                                                 , AlgorithmAndBase64EncodedSecretKey.class);
        final byte[] decodedKey = Base64.getDecoder().decode(jsonObj.encodedKey);
        final SecretKey rv = new SecretKeySpec(decodedKey, 0, decodedKey.length, jsonObj.algorithm);
        return rv;
    }


    private static String prepareJWS(final SecretKey key
                                     , final String subject
                                     , final int ttlSeconds) {
        // the compact() method "actually builds the JWT and serializes it to a compact, URL-safe string"
        final String jws = Jwts.builder()
            .setSubject(subject)
            .setExpiration(createExpirationDate(ttlSeconds))
            .signWith(key).compact();

        return jws;
    }

    private static Date createExpirationDate(final int ttlSeconds) {
        final LocalDateTime x = LocalDateTime.now().plusSeconds(ttlSeconds);
        Date rv = Date.from( x.atZone( ZoneId.systemDefault()).toInstant() );
        return rv;
    }
    
    public static Claims verifyJWS(final String algoAndSecretKeyEncoded
                                    , final String jws) throws ExpiredJwtException {
        final SecretKey key = stringToSecretKey(algoAndSecretKeyEncoded);
        return verifyJWS(key, jws);
    }

    public static Claims verifyJWS(final SecretKey key
                                    , final String jws) throws ExpiredJwtException {
        final Jws<Claims> claimsHB = Jwts.parser().setSigningKey(key).parseClaimsJws(jws);

        final Claims claims = claimsHB.getBody();
        return claims;
    }    
}
