package gr.cognitera.util.base;

import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;


public class Maybe<T> {
    public final T value;
    public Maybe(final T t) {
        this.value = t;
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("value", value)
            ;
    }

    public boolean isPresent() {
        return value!=null;
    }
 
    @Override
    public final boolean equals(Object o) {
        if (o == null) return false;
        
        if (!(o instanceof Maybe))
            return false;

        Maybe<?> other = (Maybe) o;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode(){
        return Objects.hash(value);
    }

    public static <T> Maybe<T> of(T t) {
        return new Maybe<T>(t);
    }
}
