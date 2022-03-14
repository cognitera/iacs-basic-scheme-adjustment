package gr.cognitera.util.jdbc;

public abstract class PostgreSQLAbstractDAL extends AbstractDAL {

    @Override
    protected String sqlStateForDeadlock() {
        return "40P01";
    }

    @Override
    protected int maxNumberOfDeadlockRetries() {
        return 24;
    }
}
