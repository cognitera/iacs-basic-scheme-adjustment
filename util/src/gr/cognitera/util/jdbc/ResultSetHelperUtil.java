package gr.cognitera.util.jdbc;

import java.util.Map;

import  gr.cognitera.util.base.StringUtil;
import  gr.cognitera.util.base.MapificationConfig;

public class ResultSetHelperUtil {
    private ResultSetHelperUtil() {}

    public static Map<String, String> mapify(final String s) {
        return StringUtil.mapify(s, new MapificationConfig(","
                                                           , ":"
                                                           , MapificationConfig.DuplicateKeyHandling.DUPL_KEYS_NOT_ALLOWED));
    }

}
