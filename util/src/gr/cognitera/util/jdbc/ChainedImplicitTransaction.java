package gr.cognitera.util.jdbc;


/*
 *
 * This is the connection mode where statements on the same connection are 
 * implicitly on the same transaction and we never have to issue "BEGIN TRAN".
 * Instead, when we want to end the current transaction we issue a "COMMIT" and
 * a new transaction implicitly begins at that point.
 *
 * cf. http://mperdikeas.github.io/sql-concepts.html
 */

public abstract class ChainedImplicitTransaction<T> implements ITransaction<T> {

    @Override
    public TransactionTypology getTypology() {
        return TransactionTypology.CHAINED_IMPLICIT;
    }

}
