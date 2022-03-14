package gr.cognitera.util.gson;

import java.util.Objects;

import com.google.common.base.MoreObjects;

public class NoAddedValueJSONWrapper<T> {
    public T data;
    public NoAddedValueJSONWrapper(T t) {
        this.data = t;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null) return false;
        
        if (!(o instanceof NoAddedValueJSONWrapper))
            return false;
 
        final NoAddedValueJSONWrapper<?> other = (NoAddedValueJSONWrapper) o;
        return Objects.equals(data, other.data);
    }

    @Override
    public int hashCode(){
        return Objects.hash(data);
    }
}

