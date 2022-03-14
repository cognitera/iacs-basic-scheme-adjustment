package gr.cognitera.util.base;

import java.util.Random;
import java.util.List;
import java.util.Arrays;


public class EnumUtil {

    public static <T extends Enum<T>> List<T> asImmutableList(Class<T> t) {
        return Arrays.asList(t.getEnumConstants());
    }
}
