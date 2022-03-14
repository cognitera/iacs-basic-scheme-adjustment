package gr.cognitera.util.jdbc;



public class ApplicationInducedRetry extends RuntimeException {

    public ApplicationInducedRetry() {}

    public ApplicationInducedRetry(String msg) {
        super(msg);
    }

    public ApplicationInducedRetry(Throwable t) {
        super(t);
    }

}
