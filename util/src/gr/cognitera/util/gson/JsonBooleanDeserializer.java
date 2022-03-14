package gr.cognitera.util.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;
    

class JsonBooleanDeserializer implements JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            String value = json.getAsJsonPrimitive().getAsString();
            if ("true".equals(value) || "false".equals(value)) {
                return Boolean.valueOf(value);
            } else {
                throw new JsonParseException("Cannot parse JSON [" + json.toString() + "] to boolean value");
            }
        } catch (ClassCastException e) {
            throw new JsonParseException("Cannot parse JSON [" + json.toString() + "] to boolean value", e);
        }
    }
}
