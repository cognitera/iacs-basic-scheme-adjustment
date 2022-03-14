package gr.cognitera.util.base;

import java.util.LinkedHashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.junit.Assert;

import com.google.common.collect.Iterables;
            

public class CollectionUtil {


    public static final <T> LinkedHashSet<T> uniqueValues(Collection<T> ts) {
        return new LinkedHashSet<T>(ts);
    }

    public static final <T> boolean hasDuplicates(Collection<T> ts) {
        LinkedHashSet<T> uniqueValues = uniqueValues(ts);
        Assert.assertTrue(uniqueValues.size()<=ts.size());
        return uniqueValues.size()<ts.size();
    }

    public static final <T> T theAtMost1UniqueValue(Collection<T> ts) {
        LinkedHashSet<T> uniqueValues = uniqueValues(ts);
        Assert.assertTrue(uniqueValues.size()<=1);
        if (uniqueValues.isEmpty())
            return null;
        else
            return exactlyOne(ts);
    }
    
    public static final <T> T exactlyOne(Collection<T> ts) {
        Assert.assertEquals(1, ts.size());
        return Iterables.get(ts, 0);

    }

    /**
     * Asserts that two collections are equal in order. I.e. they need to have the same number
     * of elements and the Objects.equals method must be true for all pairs (when the elements
     * of the first collection are supplied as the first argument to Objects.equals and the
     * elements of the second collection are supplied as the second argument).
     * @param <T> the type of elements in the first collection
     * @param <V> the type of elements in the second collection
     * @param ts the first collection
     * @param vs the second collection
     *
     */
    public static <T, V> void assertEqualsInOrder(Collection<T> ts, Collection<V> vs) {
        Assert.assertEquals(ts.size(), vs.size());
        Iterator<T> itt = ts.iterator();
        Iterator<V> itv = vs.iterator();
        int i = 0;
        while (itt.hasNext()) {
            Assert.assertTrue(itv.hasNext());
            T t = itt.next();
            V v = itv.next();
            Assert.assertEquals(String.format("The two collections differ on index %d"
                                              , i)
                                , t, v);
            i++;
        }
    }

    public static <T> List<T> onlyNonNull(Collection<T> vs) {
        final List<T> rv = new ArrayList<>();
        for (final T v: vs) {
            if (v!=null)
                rv.add(v);
        }
        return rv;
    }
}
