package gr.cognitera.util.base;

import org.junit.Assert;

/**
 * Static Code Analysis utils
 * These come handy to satisfy the javac code analysis in certain situations.
 *
 */
public final class SCAUtils {
    private SCAUtils() {}

    public static <T> T CANT_REACH_THIS_LINE(Class<T> klass) {
        Assert.fail("execution's not supposed to reach this line");
        return (T) null;
    }

    public static Error EXCEPTION_FOR_LINE_THAT_WILL_NEVER_BE_REACHED() {
        return new Error("impossible to reach this line - this is just to satisfy the compiler");
    }

}
