package gr.cognitera.util.rest;

import java.util.List;
import java.util.ArrayList;

import org.junit.Assert;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class NameValuePairHelper {


    public static List<NameValuePair> create(Object ...nameValuePairs) {
        Assert.assertEquals(String.format("Was expecting the number of parameters to be even, yet it was: %d"
                                          , nameValuePairs.length)
                            , 0
                            , nameValuePairs.length %2);
        List<NameValuePair> rv = new ArrayList<>();
        for (int i = 0 ; i < nameValuePairs.length /2 ; i++) {
            final int iName  = 2*i;
            final int iValue = 2*i+1;
            String name;
            {
                final Object nameO = nameValuePairs[iName];
                Assert.assertNotNull(nameO);
                Assert.assertEquals(String.class, nameO.getClass());
                name = (String) nameO;
            }
            String value;
            {
                final Object valueO = nameValuePairs[iValue];
                if (valueO == null)
                    continue;
                value  = valueO.toString();
            }
            rv.add(new BasicNameValuePair(name, value));
        }
        return rv;
    }

}
