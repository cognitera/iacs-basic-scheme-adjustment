package gr.cognitera.util.gson;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Type;

import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;


public class MapAdapter<K, V> implements JsonSerializer<Map<K, V>>, JsonDeserializer<Map<K, V>> {

    @Override
    public JsonElement serialize(Map<K, V> m, Type typeOfT, JsonSerializationContext context) {
        JsonArray rv = new JsonArray();
        for (final Map.Entry<K, V> entry : m.entrySet()) {
            final K k = entry.getKey();
            final V v = entry.getValue();
            final JsonObject kv = new JsonObject();
            kv.add        ("k"     , context.serialize(k));
            kv.addProperty("ktype" , k==null?null:k.getClass().getName());
            kv.add        ("v"     , context.serialize(v));
            kv.addProperty("vtype" , v==null?null:v.getClass().getName());
            rv.add(kv);
        }
        return rv;
    }

    @Override
    public Map<K, V> deserialize(JsonElement _json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonArray json = _json.getAsJsonArray();
        Map<K, V> rv = new HashMap<>();
        for (int i = 0 ; i < json.size() ; i++) {
            JsonObject o = (JsonObject) json.get(i);
            String ktype = AdapterUtil.getStringOrNull(o, "ktype");
            String vtype = AdapterUtil.getStringOrNull(o, "vtype");
            // we can't use ternary operator below, see: http://stackoverflow.com/questions/19305095/type-mismatch-cannot-convert-from-object-to-k-when-using-conditional-operator
            K k = null;
            if (ktype != null)
                k = context.deserialize( o.get("k"), AdapterUtil.classForName(ktype));
            V v = null;
            if (vtype != null)
                v = context.deserialize( o.get("v"), AdapterUtil.classForName(vtype));
            rv.put(k, v);
        }
        return rv;
    }
    
}
