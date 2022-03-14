package gr.cognitera.util.base;

import java.security.SecureRandom;
import java.math.BigInteger;

/**
 *
 * This is a cryptographically secure random string generator
 *
 */
public class RandomStringGenerator implements IRandomStringGenerator {

    private SecureRandom random;
    
    public RandomStringGenerator() {
        this(System.currentTimeMillis());
    }

    public RandomStringGenerator(final long seed) {
        random = new SecureRandom();
        random.setSeed(seed);
    }

    @Override
    public String next(final int bitsOfEntropy) {
        return new BigInteger(bitsOfEntropy, random).toString(Character.MAX_RADIX);
    }

    public static String staticNext(final int bitsOfEntropy) {
        return (new RandomStringGenerator()).next(bitsOfEntropy);
    }
}
