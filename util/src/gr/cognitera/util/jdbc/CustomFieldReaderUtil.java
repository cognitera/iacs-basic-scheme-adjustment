package gr.cognitera.util.jdbc;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;

import gr.cognitera.util.base.Util;


public class CustomFieldReaderUtil {

    public static <T> Map<String, CustomFieldReader<?>> prepareFieldOverrides(final Class<T> klass
                                                                              , final CustomFieldReader<?> ...customFieldReaders) {
        assertThatClassesAreUnique(customFieldReaders);
        final Map<String, CustomFieldReader<?>> rv = new LinkedHashMap<>();
        for (final Field field : klass.getFields()) {
            final CustomFieldReader cfr = getCustomFieldReaderForClass(field.getType(), customFieldReaders);
            if (cfr!=null) {
                Assert.assertNull(String.format("Impossible to encounter field name [%s] twice"
                                                , field.getName())
                                  , rv.put(field.getName(), cfr));
            }
        }
        sanityCheckForFieldOverrides(klass, rv);
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

    public static Map<String, CustomFieldReader<?>> prepareFieldOverrides(Object ... columnLabelsAndCustomFieldReaders) {
        Map<String, CustomFieldReader<?>> rv = new LinkedHashMap<>();
        boolean customFieldReaderMayAppear = false;
        List<String> columnLabelsForThisCustomFieldReader = new ArrayList<>();
        for (int i = 0 ; i < columnLabelsAndCustomFieldReaders.length ; i++) {
            Object o = columnLabelsAndCustomFieldReaders[i];
            if (o.getClass()==String.class) {
                columnLabelsForThisCustomFieldReader.add((String) o);
                customFieldReaderMayAppear = true;
            } else if (o instanceof CustomFieldReader) {
                Assert.assertTrue(String.format("On index [%d] I was not expecting a [%s] yet; yet an object of class [%s] was encountered"
                                                , i
                                                , CustomFieldReader.class.getName()
                                                , o.getClass().getName())
                                  , customFieldReaderMayAppear);
                Assert.assertTrue(String.format("On index [%d] I was expecting at least 1 columnLabel to have been accumulated, yet none were present"
                                                 , i)
                                  , !columnLabelsForThisCustomFieldReader.isEmpty());
                CustomFieldReader customFieldReader = (CustomFieldReader) o;
                for (String columnLabelForThisCustomFieldReader: columnLabelsForThisCustomFieldReader) {
                    Assert.assertNull(String.format("Column label [%s] already encountered"
                                                    , columnLabelForThisCustomFieldReader)
                                      , rv.put(columnLabelForThisCustomFieldReader, customFieldReader));
                }
                columnLabelsForThisCustomFieldReader.clear();
                customFieldReaderMayAppear = false;
            } else
                Assert.fail(String.format("On index [%d] an object of class [%s] (which I've no use of) was encountered"
                                          , i
                                          , o.getClass().getName()));
        }
        return rv;
    }

    public static void sanityCheckForFieldOverrides(Class<?> klass, Map<String, CustomFieldReader<?>> fieldOverrides) {
        try {
            for (Map.Entry<String, CustomFieldReader<?>> entry: fieldOverrides.entrySet()) {
                final String fieldOverrideColumnLabel        = entry.getKey();
                final CustomFieldReader<?> customFieldReader = entry.getValue();
                Field field = klass.getField(fieldOverrideColumnLabel);
                Class<?> fieldClass = field.getType();
                Assert.assertTrue(String.format("The class of field [%s] in class [%s] was [%s] whereas the customFieldReader of class [%s] can only handle class [%s]"
                                                , field.getName()
                                                , klass.getName()
                                                , fieldClass.getName()
                                                , customFieldReader.getClass().getName()
                                                , customFieldReader.getHandledClass().getName())
                                  ,fieldClass == customFieldReader.getHandledClass());
                // don't worry about assignability at this point, strict equality will do for the time being
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    

}
