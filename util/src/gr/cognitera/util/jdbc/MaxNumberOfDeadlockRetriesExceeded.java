package gr.cognitera.util.jdbc;


public class MaxNumberOfDeadlockRetriesExceeded extends RuntimeException {


    public MaxNumberOfDeadlockRetriesExceeded(final String msg) {
        super(msg);
    }


}
