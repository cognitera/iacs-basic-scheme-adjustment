package gr.cognitera.util.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParseException;

public final class AdapterUtil {
    private AdapterUtil() {}
    public static Class<?> classForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(className, e);
        }
    }
    public static String getStringOrNull(JsonObject o, String property) {
        JsonElement el = o.get(property);
        if (el instanceof JsonNull)
            return null;
        else if (el instanceof JsonPrimitive) {
            return ( (JsonPrimitive) el).getAsString();
        } else throw new RuntimeException(String.format("%s|%s", property, el.getClass().getName()));
    }
}
