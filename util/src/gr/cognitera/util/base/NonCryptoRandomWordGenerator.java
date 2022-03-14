package gr.cognitera.util.base;

import java.util.Random;
import java.util.Locale;

/**
 *
 * This is a non-cryptographically secure random string generator
 *
 */
public final class NonCryptoRandomWordGenerator {

    private final String alphabet;
    private final Random random;

    public static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    public static final String DIGITS = "0123456789";
    
    public NonCryptoRandomWordGenerator() {
        this(LETTERS.toUpperCase(Locale.US)+LETTERS+DIGITS, Util.now());
    }

    public NonCryptoRandomWordGenerator(final String alphabet, final long seed) {
        this.alphabet = alphabet;
        this.random = new Random();
        this.random.setSeed(seed);
    }

    public String next(final int length) {
        return next(random, alphabet, length);
    }

    public static String next(final Random r, final String alphabet, final int length) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length ; i++)
            sb.append(alphabet.charAt(r.nextInt(Integer.MAX_VALUE)%alphabet.length()));
        return sb.toString();
    }
}
