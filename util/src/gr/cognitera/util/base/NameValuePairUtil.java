package gr.cognitera.util.base;

import java.util.List;
import java.util.ArrayList;

import com.google.common.base.Joiner;
import com.google.common.base.Function;

import org.apache.http.NameValuePair;

public final class NameValuePairUtil {


    private NameValuePairUtil() {}

    public static final String stringify(List<NameValuePair> nvps, Function<String, String> valueMutator) {
        final List<String> rv = new ArrayList<>();
        for (final NameValuePair nvp: nvps) {
            final String s = String.format("(name: [%s], value: [%s])"
                                           , nvp.getName()
                                           , valueMutator.apply(nvp.getValue()));
            rv.add(s);
        }
        return Joiner.on(", ").join(rv);
    }
}
