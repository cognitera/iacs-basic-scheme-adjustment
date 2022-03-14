package gr.cognitera.util.base;

public class Holder<T> {

    private T t;

    public Holder(final T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public void set(final T t) {
        this.t = t;
    }

    public void unset() {
        this.t = null;
    }

    public boolean isSet() {
        return this.t!=null;
    }

}
