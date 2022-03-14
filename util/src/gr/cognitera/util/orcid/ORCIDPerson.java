package gr.cognitera.util.orcid;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParser;
                        
public class ORCIDPerson {

    public final String fname;
    public final String lname;

    public ORCIDPerson(final String fname, final String lname) {
        this.fname = fname;
        this.lname = lname;
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("fname", fname)
            .add("lname", lname)
            ;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null) return false;
        
        if (!(o instanceof ORCIDPerson))
            return false;
 
        final ORCIDPerson other = (ORCIDPerson) o;
        return Objects.equals(fname, other.fname) &&
               Objects.equals(lname, other.lname) ;
    }

    @Override
    public int hashCode(){
        return Objects.hash(fname, lname);
    }

    /**
     * Extracts the fields necessary to create an object of this class from the
     * GET response (with a JSON Accept header in the request) of the ORCID server on
     * the following endpoint (mutatis mutandis):
     * <p>
     * <a href="https://HOST/v2.1/ORCID/person">https://HOST/v2.1/ORCID/person</a>
     * <p>
     * The XSDs are given
     * <a href="https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/README.md#xsds-and-current-state-all-stable">here</a> and out of them, the JSON structure can, also, be inferred.
     *
     * @param json the JSON representation of a researcher's personal data under ORCID 
     * @return the corresponding instance of this class that encodes (a subset of) the information found in 
     *         the JSON representation
     *
     */
    public static ORCIDPerson fromJson(String json) throws CannotConstructORCIDPersonFromJson {
        final JsonElement jelement = new JsonParser().parse(json);
        final JsonElement nameElement = jelement.getAsJsonObject().get("name");
        if (!nameElement.isJsonObject()) {
            throw new CannotConstructORCIDPersonFromJson(json, "field [name] is not a JSON object as expected");
        } else {
            final JsonObject nameObject = nameElement.getAsJsonObject();
            final String fname = getJsonObjectThenString(json, nameObject, "given-names", "value");
            final String lname = getJsonObjectThenString(json, nameObject, "family-name", "value");
            return new ORCIDPerson(fname, lname);
        }
    }

    private static String getJsonObjectThenString(final String json
                                                  , final JsonObject o
                                                  , final String nameOfJsonObjectField
                                                  , final String nameOfJsonStringField) throws CannotConstructORCIDPersonFromJson {
        final JsonElement jsonObjectAsElement = o.get(nameOfJsonObjectField);
        if (!jsonObjectAsElement.isJsonObject()) {
            throw new CannotConstructORCIDPersonFromJson(json
                                                         , String.format("field [%s] is not a JsonObject as expected"
                                                                         , nameOfJsonObjectField)
                                                         );
        } else {
            final JsonObject jsonObject = jsonObjectAsElement.getAsJsonObject();
            final JsonElement jsonStringAsElement = jsonObject.get(nameOfJsonStringField);
            if (!jsonStringAsElement.isJsonPrimitive()) {
                throw new CannotConstructORCIDPersonFromJson(json
                                                             , String.format("field [%s] is not a JsonPrimitive as expected"
                                                                             , nameOfJsonStringField)
                                                             );
            } else {
                final JsonPrimitive jsonStringAsPrimitive  = jsonStringAsElement.getAsJsonPrimitive();
                if (!jsonStringAsPrimitive.isString()) {
                    throw new CannotConstructORCIDPersonFromJson(json
                                                                 , String.format("field [%s] is a JsonPrimitive but not a JsonString as expected"
                                                                                 , nameOfJsonStringField)
                                                                 );
                } else {
                    return jsonStringAsPrimitive.getAsString();
                }
            }
        }
    }
    
}
