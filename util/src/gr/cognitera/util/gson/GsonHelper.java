package gr.cognitera.util.gson;

import java.util.Map;
import java.lang.reflect.Type;

import com.google.gson.JsonParseException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gr.cognitera.util.base.Pair;

public class GsonHelper {

    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private static       Gson gsonExt;     /*
                                            *  Extended Gson with:
                                            *     [ i] custom adapters for Map and Pair, allowing Pair objects in Map keys
                                            *     [ii] strict Boolean deserializer
                                            *
                                            */  

    private GsonHelper() {}

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        // gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter( Pair   .class, new PairAdapter());
        gsonBuilder.registerTypeAdapter( Map    .class, new MapAdapter());
        gsonBuilder.registerTypeAdapter( Boolean.class, new JsonBooleanDeserializer());
        gsonBuilder.registerTypeAdapter( boolean.class, new JsonBooleanDeserializer());        
        gsonExt = gsonBuilder.serializeNulls().create();
    }

    public static String toJson(Object o) {
        return gson.toJson(o);
    }
    public static String toJsonExt(Object o) {
        return gsonExt.toJson(o);
    }
    /*
      The way to create and then use arbitrary Type objects is the following:
      import java.lang.reflect.Type;
      import com.google.gson.reflect.TypeToken;
      final Type TYPE= new TypeToken<Pair<Boolean, List<Qr_ede_hd>>>() {}.getType();
      Pair<Boolean, List<Qr_ede_hd>> retValue = JsonProvider.fromJson(json, TYPE);
    */
    public static String toJson(Object o, Type t) {
        return gson.toJson(o, t);
    }
    public static String toJsonExt(Object o, Type t) {
        return gsonExt.toJson(o, t);
    }
	
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return _fromJson(gson, json, classOfT);
    }
    public static <T> T fromJsonExt(String json, Class<T> classOfT) {
        return _fromJson(gsonExt, json, classOfT);
    }

    private static <T> T _fromJson(Gson gson, String json, Class<T> classOfT) {
        @SuppressWarnings("unchecked")	  
            T target = (T) gson.fromJson(json, (Type) classOfT);
        return target; // NOPMD
    }

    public static <T> T fromJson(String json, Type t) {
        return _fromJson(gson, json, t);
    }

    public static <T> T fromJsonExt(String json, Type t) {
        return _fromJson(gsonExt, json, t);
    }

    private static <T> T _fromJson(Gson gson, String json, Type t) {
        @SuppressWarnings("unchecked")	
            T target = (T) gson.fromJson(json, t);
        return target; // NOPMD
    }
    
}
