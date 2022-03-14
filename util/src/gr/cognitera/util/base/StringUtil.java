package gr.cognitera.util.base;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Arrays;
import java.text.NumberFormat;
import java.text.ParseException;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.junit.Assert;

import com.google.common.base.Joiner;
import com.google.common.base.Function;
import com.google.common.primitives.Doubles;



public final class StringUtil {

    private StringUtil() {}

    public static final boolean isNotEmptyStringButMayBeNull(final String s) {
        return (s==null)||( !s.equals("") );
    }

    public static final boolean isNeitherEmptyStringNorWhitespaceButMaybeNull(final String s) {
        return (s==null) || isNotEmptyStringButMayBeNull(s.trim());
    }

    public static final boolean isNeitherNullNorEmptyStringNorWhitespace(final String s) {
        return (s!=null) && ( !s.trim().equals("") );
    }    

    public static final boolean isNotNullAndIsEitherEmptyStringOrWhitespace(final String s) {
        return !isNeitherEmptyStringNorWhitespaceButMaybeNull(s);
    }

    public static final String stringify(final Map<String, List<String>> maps) {
        return stringify(maps, "\n", " --> ", ", ", "NULL");
    }

    public static final String stringify(final Map<String, List<String>> maps
                                         , final String keyValuesSeparator
                                         , final String keyValuesJoiner
                                         , final String valuesSeparator
                                         , final String useForNull) {
        final List<String> rv = new ArrayList<>();
        for (final Map.Entry<String, List<String>> entry : maps.entrySet()) {
            final String       k = entry.getKey();
            final List<String> v = entry.getValue();
            rv.add(String.format("%s%s%s"
                                 , k
                                 , keyValuesJoiner
                                 , Joiner.on(valuesSeparator).useForNull(useForNull).join(v)));
        }
        return Joiner.on(keyValuesSeparator).join(rv);
    }

    public static final Map<String, String> mapify(final String s) {
        return mapify(s, new MapificationConfig(";", "=", MapificationConfig.DuplicateKeyHandling.DUPL_KEYS_NOT_ALLOWED));
    }
    public static final Map<String, String> mapify(final String s
                                                   , final MapificationConfig mapificationConfig) {
        if (s==null)
            return new LinkedHashMap<>();
        final Map<String, String> rv = new LinkedHashMap<>();
        final String[] keysValues = s.split(mapificationConfig.chunkSeparator);
        for (int i = 0 ; i < keysValues.length ; i++) {
            final String[] keyValue = keysValues[i].split(mapificationConfig.keyValueSeparator);
            if (keyValue.length != 2)
                throw new IllegalArgumentException(String.format("chunk #%d (zero-indexed): [%s] consists of [%d] components"
                                                                 +" instead of the expected 2 (name-value)"
                                                                 , i
                                                                 , keysValues[i]
                                                                 , keyValue.length));
            else {
                final String key       = keyValue[0];
                final String value     = keyValue[1];
                final String prevValue = rv.put(key, value);
                if (prevValue!=null) {
                    switch (mapificationConfig.duplicateKeyHandling) {
                    case DUPL_KEYS_NOT_ALLOWED:
                        throw new IllegalArgumentException(String.format("key [%s] appears more than once in [%s]"
                                                                         , key
                                                                         , s));
                    case TOLERATE_DUPL_KEYS_ONLY_IF_VALUES_IDENTICAL:
                        if (!prevValue.equals(value))
                            throw new IllegalArgumentException(String.format("key [%s] is associated with at least two"
                                                                             +" different values in [%s]: [%s] and [%s]"
                                                                             , key
                                                                             , s
                                                                             , prevValue
                                                                             , value));
                        break;
                    case TOLERATE_DUPL_KEYS_LAST_VALUE_WINS:
                        // do nothing
                        break;
                    default:
                        Assert.fail(String.format("unhandled case: [%s]", mapificationConfig.duplicateKeyHandling));
                    }
                }
            }
        }
        return rv;
    }

    

    public static final List<String> commaSeparatedValues(final String s, final boolean trim) {
        List<String> _rv = Arrays.asList(s.split(","));
        List<String> rv = new ArrayList<>();
        for (String x: _rv) {
            rv.add(trim?x.trim():x);
        }
        return rv;
    }

    public static final boolean isInteger(final String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static final boolean isNumber(final String s) {
        final Double v = Doubles.tryParse(s);
        return v!=null;
    }

    public static final String trimAndCompressWhitespace(final String s) {
        return s==null?null:s.replaceAll("\\s+", " ").trim();
    }

    public static final String stringify(final StackTraceElement[] stackTraceElements) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < stackTraceElements.length ; i++) {
            sb.append(String.format("%s%n", stackTraceElements[i].toString()));
        }
        return sb.toString();
    }

    // we don't need a utility methoc for ASCII as Guava supplies that
    public static final boolean isISO_8859_1(final String s) {
        final CharsetEncoder encoder = Charset.forName("ISO-8859-1").newEncoder();
        return encoder.canEncode(s);
    }

    public static final String hideAllButFirstLast(final String s, int first, int last) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if ((i<first) || (i>=s.length()-last))
                sb.append(s.substring(i, i+1));
            else
                sb.append("*");
        }
        return sb.toString();
    }

    public static final String joinWtLastDelim(List<String> strings, String delim, String lastDelim) {
        if (strings.isEmpty())
            return null;
        else if (strings.size()<=2)
            return Joiner.on(lastDelim).join(strings);
        else {
            int last = strings.size()-1;
            return Joiner.on(lastDelim).join(Joiner.on(delim).join(strings.subList(0, last))
                                             , strings.get(last));
        }
    }

    public static final String clipIfNecessary(final String s, final int first, final int last) {
        final Function<String, String> stringClipper = new StringClipper(first, last);
        return stringClipper.apply(s);
    }
}
