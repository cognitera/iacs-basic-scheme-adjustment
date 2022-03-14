package gr.cognitera.util.base;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import java.io.Serializable;

public final class Triad<A, B, C> implements Serializable {
	 
    private static final long serialVersionUID = 1L;
	
    public A a;
    public B b;
    public C c;
 
    // no args constructor needed by Json 
    public Triad() {
    	super();
    }
    public Triad(final A a, final B b, final C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
    
    public static <A, B, C> Triad<A, B, C> create(final A a, final B b, final C c) {
        return new Triad<A, B, C>(a, b, c);
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("a", a)
            .add("b", b)
            .add("c", c)
            ;
    }
 
    @Override
    public final boolean equals(Object o) {
        if (o == null) return false;
        
        if (!(o instanceof Triad))
            return false;
 
        final Triad<?, ?, ?> other = (Triad) o;
        return Objects.equals(a, other.a) &&
            Objects.equals(b, other.b) &&
            Objects.equals(c, other.c)
            ;
    }

    @Override
    public int hashCode(){
        return Objects.hash(a, b, c);
    }

}
