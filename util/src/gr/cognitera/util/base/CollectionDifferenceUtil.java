package gr.cognitera.util.base;

import java.util.LinkedHashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;

import java.util.function.Function;

            

public final class CollectionDifferenceUtil {

    private CollectionDifferenceUtil() {}

    public static <A, ID> List<ID> diff(Collection<A> l1
                                       , Collection<A> l2
                                       , Function<A,ID> id) {
        return diff(l1, l2, id, id, id);
    }    

    public static <A, ID, R> List<R> diff(Collection<A> l1
                                          , Collection<A> l2
                                          , Function<A,ID> id
                                          , Function<A,R> r) {
        return diff(l1, l2, id, id, r);
    }

    public static <A,B,R,ID> List<R> diff(Collection<A> l1
                                          , Collection<B> l2
                                          , Function<A,ID> aID
                                          , Function<B,ID> bID
                                          , Function<A,R> r) {

        Set<ID> b=l2.stream().map(bID).collect(Collectors.toSet());
        return l1.stream().filter(a -> !b.contains(aID.apply(a)))
            .map(r).collect(Collectors.toList());
    }

    public static <A, ID> List<ID> sameReportA(Collection<A> l1
                                       , Collection<A> l2
                                       , Function<A,ID> id) {
        return sameReportA(l1, l2, id, id);
    }    

    public static <A, ID, R> List<R> sameReportA(Collection<A> l1
                                          , Collection<A> l2
                                          , Function<A,ID> id
                                          , Function<A,R> r) {
        return sameReportA(l1, l2, id, id, r);
    }

    public static <A,B,R,ID> List<R> sameReportA(Collection<A> l1
                                          , Collection<B> l2
                                          , Function<A,ID> aID
                                          , Function<B,ID> bID
                                          , Function<A,R> r) {

        Set<ID> b=l2.stream().map(bID).collect(Collectors.toSet());
        return l1.stream().filter(a -> b.contains(aID.apply(a)))
            .map(r).collect(Collectors.toList());
    }

    public static <A,B,ID,RA,RB,RAB> CollectionDifference<RA, RB, RAB> computeDifference(Collection<A> l1
                                          , Collection<B> l2
                                          , Function<A,ID> aID
                                          , Function<B,ID> bID
                                          , Function<A,RA> rA
                                          , Function<B,RB> rB
                                          , Function<A,RAB> rAB) {
        final List<RA> inAnotinB = diff(l1, l2, aID, bID, rA);
        final List<RB> inBnotinA = diff(l2, l1, bID, aID, rB);
        final List<RAB> inBoth    = sameReportA(l1, l2, aID, bID, rAB);
        return new CollectionDifference<RA, RB, RAB>(inAnotinB, inBnotinA, inBoth);
        
    }
}
