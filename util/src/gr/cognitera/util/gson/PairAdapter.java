package gr.cognitera.util.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;


import gr.cognitera.util.base.Pair;

public class PairAdapter<A, B> implements JsonSerializer<Pair<A, B>>, JsonDeserializer<Pair<A, B>> {

    @Override
    public JsonElement serialize(Pair<A, B> p, Type typeOfT, JsonSerializationContext context) {
        JsonObject rv = new JsonObject();
        rv.add        ("a"     , context.serialize(p.a));
        rv.addProperty("atype" , p.a==null?null:p.a.getClass().getName());
        rv.add        ("b"     , context.serialize(p.b));
        rv.addProperty("btype" , p.b==null?null:p.b.getClass().getName());
        return rv;
    }

    @Override
    public Pair<A, B> deserialize(JsonElement _json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject json = _json.getAsJsonObject();
        String atype = AdapterUtil.getStringOrNull(json, "atype"); 
        String btype = AdapterUtil.getStringOrNull(json, "btype"); 

        A a = null;
        if (atype!=null)
            a = context.deserialize( json.get("a"), AdapterUtil.classForName(atype));
        B b = null;
        if (btype!=null)
            b = context.deserialize( json.get("b"), AdapterUtil.classForName(btype));
        return Pair.create(a, b);
    }
}
