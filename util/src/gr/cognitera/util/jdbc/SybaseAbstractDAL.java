package gr.cognitera.util.jdbc;

public abstract class SybaseAbstractDAL extends AbstractDAL {

    
    @Override
    protected int errorCodeForDeadlock() {
        return 1205;
    }

    @Override
    protected int maxNumberOfDeadlockRetries() {
        return 24;
    }
}
