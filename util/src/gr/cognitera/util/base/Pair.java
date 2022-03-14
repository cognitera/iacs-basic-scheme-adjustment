package gr.cognitera.util.base;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import java.io.Serializable;

public class Pair<A, B> implements Serializable {
	 
    private static final long serialVersionUID = 1L;
	
    public A a;
    public B b;
 
    // no args constructor needed by Json 
    public Pair() {
    	super();
    }
    public Pair(final A a, final B b) {
        this.a = a;
        this.b = b;
    }
    
    public static <A, B> Pair<A, B> create(A a, B b) {
        return new Pair<A, B>(a, b);
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("a", a)
            .add("b", b)
            ;
    }
 
    @Override
    public final boolean equals(Object o) {
        if (o == null) return false;
        
        if (!(o instanceof Pair))
            return false;
 
        final Pair<?, ?> other = (Pair) o;
        return Objects.equals(a, other.a) &&
            Objects.equals(b, other.b) ;
    }

    @Override
    public int hashCode(){
        return Objects.hash(a, b);
    }

}
