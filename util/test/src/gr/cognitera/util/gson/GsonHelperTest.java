package gr.cognitera.util.gson;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.everyItem;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.net.URL;
import java.util.Objects;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import org.junit.rules.ErrorCollector;
import org.junit.Rule;

import javax.xml.bind.DatatypeConverter;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;


import java.util.Map;
import java.util.LinkedHashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.ScriptContext;
import java.io.StringWriter;


class A {
    public String a1;
    public int a2;
    public Boolean a3;

    public A(final String a1, final int a2, final Boolean a3) {
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null) return false;
        
        if (!(o instanceof A))
            return false;
 
        final A other = (A) o;
        return Objects.equals(a1, other.a1) &&
            Objects.equals(a2, other.a2) &&
            Objects.equals(a3, other.a3);
    }

    @Override
    public int hashCode(){
        return Objects.hash(a1, a2, a3);
    }
}

class B {
    public A b1;
    public B(A b1) {
        this.b1 = b1;
    }
    @Override
    public final boolean equals(Object o) {
        if (o == null) return false;
        
        if (!(o instanceof B))
            return false;
 
        final B other = (B) o;
        return Objects.equals(b1, other.b1);
    }

    @Override
    public int hashCode(){
        return Objects.hash(b1);
    }        
}

class JSONValueAndOriginalClass<T> {

    public final String   jsonStr;
    public final Class<T> klass;

    public JSONValueAndOriginalClass(final String jsonStr, final Class<T> klass) {
        this.jsonStr  = jsonStr;
        this.klass    = klass;
    }

}





public class GsonHelperTest {

    @Test
    public void demonstrateThatClassesAreVisible() {
        final GsonHelper x = null;
    }

    @Test
    public void testThatToJSONandBackAgainWorksInJava() throws ScriptException {
        Map<Object, JSONValueAndOriginalClass<?>> kvs = new LinkedHashMap<>();
        kvs.put("foo"                             , new JSONValueAndOriginalClass("\"foo\"", String.class));
        kvs.put("he said: \"I hate you\""         , new JSONValueAndOriginalClass("\"he said: \\\"I hate you\\\"\"", String.class));
        kvs.put("foo\nboo"                        , new JSONValueAndOriginalClass("\"foo\\nboo\"", String.class));
        kvs.put(new NoAddedValueJSONWrapper(null) , new JSONValueAndOriginalClass("{\"data\":null}", NoAddedValueJSONWrapper.class));
        kvs.put(new NoAddedValueJSONWrapper("foo"), new JSONValueAndOriginalClass("{\"data\":\"foo\"}", NoAddedValueJSONWrapper.class));
        kvs.put(new A("answer", 42, true)         , new JSONValueAndOriginalClass("{\"a1\":\"answer\",\"a2\":42,\"a3\":true}", A.class));        

        // test that JSON to and from works in Java->Java->Java
        for (Object k: kvs.keySet()) {
            final String v    = kvs.get(k).jsonStr;
            final Class klass = kvs.get(k).klass;
            final String actual = GsonHelper.toJson(k);
            System.out.printf("GSON helper tests, actual=[%s]\n", actual);
            Assert.assertEquals(v, actual);
            final Object o = GsonHelper.fromJson(v, klass);
            Assert.assertEquals(k, o);
            Assert.assertEquals(v, GsonHelper.toJson(o)); // this is redundant at this point, really
        }

        // test that JSON to and from works in Java -> JS
        for (Object k: kvs.keySet()) {
            final String jsonStrExpected = kvs.get(k).jsonStr;
            final Class klass = kvs.get(k).klass;
            final String jsonStrActual = GsonHelper.toJson(k);
            System.out.printf("GSON helper tests, actual=[%s]\n", jsonStrActual);
            Assert.assertEquals(jsonStrExpected, jsonStrActual);
            final ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
            final ScriptContext context = engine.getContext();
            final StringWriter writer = new StringWriter();
            context.setWriter(writer);

            /*
             * Notice how we are embedding the stringified JSON object directly
             * into JS code - you don't need to quote it or anything - it's a perfectly
             * valid JSON literal, hence a valid JS object in its own right.
             *  |
             *  +--------------------------------+
             *                                   |
             *                                  \/        */
            engine.eval(String.format("jsonObj = %s; print(JSON.stringify(jsonObj));", jsonStrActual));

            final String output = writer.toString().trim(); // trim() is necessary as the print function in JS is apparently adding a line break at the end.

            System.out.printf("Script output for json string value [%s] is: [%s]\n"
                              , jsonStrActual
                              , output);
            Assert.assertEquals(jsonStrActual, output);
        }        
    }

    @Test
    public void testNestedObjects() {
        final B b = new B(new A("the answer is: \"42\"", 42, true));
        Assert.assertEquals(GsonHelper.fromJson(GsonHelper.toJson(b), B.class), b);
    }
}
