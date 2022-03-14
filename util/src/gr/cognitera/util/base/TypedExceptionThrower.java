package gr.cognitera.util.base;

public class TypedExceptionThrower {

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwIfTypeMatch(Throwable t, Class<T> klass) throws T {
        if (klass.isInstance(t))
            throw (T) t;
    }

    public static <T1 extends Throwable> void throwOneOf(Throwable t, Class<T1> klass1) throws T1 {
        throwIfTypeMatch(t, klass1);
        throw new RuntimeException(t);
    }    
    
    public static <T1 extends Throwable, T2 extends Throwable> void throwOneOf(Throwable t, Class<T1> klass1, Class<T2> klass2) throws T1, T2 {
        throwIfTypeMatch(t, klass1);
        throwIfTypeMatch(t, klass2);
        throw new RuntimeException(t);
    }

    public static <T1 extends Throwable, T2 extends Throwable, T3 extends Throwable> void throwOneOf(Throwable t, Class<T1> klass1, Class<T2> klass2, Class<T3> klass3) throws T1, T2, T3 {
        throwIfTypeMatch(t, klass1);
        throwIfTypeMatch(t, klass2);
        throwIfTypeMatch(t, klass3);
        throw new RuntimeException(t);
    }        

}
