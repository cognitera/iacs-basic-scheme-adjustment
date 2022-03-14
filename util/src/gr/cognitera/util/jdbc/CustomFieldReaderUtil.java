package gr.cognitera.util.jdbc;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.apache.log4j.Logger;

import gr.cognitera.util.base.Util;


public class CustomFieldReaderUtil {

    public static <T> Map<String, CustomFieldReader<?>> prepareFieldOverrides(final Class<T> klass
                                                                              , final CustomFieldReader<?> ...customFieldReaders) {
        assertThatClassesAreUnique(customFieldReaders);
        final Map<String, CustomFieldReader<?>> rv = new LinkedHashMap<>();
        for (final Field field : klass.getFields()) {
            final CustomFieldReader cfr = getCustomFieldReaderForClass(field.getClass(), customFieldReaders);
            if (cfr!=null)
                Assert.assertNull(String.format("Impossible to encounter field name [%s] twice"
                                                , field.getName())
                                  , rv.put(field.getName(), cfr));
        }
        return rv;
    }

    private static CustomFieldReader<?> getCustomFieldReaderForClass(final Class<?> klass, final CustomFieldReader<?>[] customFieldReaders) {
        CustomFieldReader<?> rv = null;
        for (CustomFieldReader<?> customFieldReader: customFieldReaders) {
            final Class<?> klass2 = customFieldReader.getHandledClass();
            if (klass == klass2) {
                Assert.assertNull(rv);
                rv = customFieldReader;
            }
        }
        return rv;
    }

    private static void assertThatClassesAreUnique(final CustomFieldReader<?>[] customFieldReaders) {
        final Set<Class<?>> classes = new HashSet<>();
        for (CustomFieldReader<?> customFieldReader: customFieldReaders) {
            final Class<?> klass = customFieldReader.getHandledClass();
            Assert.assertTrue(String.format("Class [%s] encountered twice"
                                            , klass.getName())
                              , classes.add(klass));
        }
    }

}
