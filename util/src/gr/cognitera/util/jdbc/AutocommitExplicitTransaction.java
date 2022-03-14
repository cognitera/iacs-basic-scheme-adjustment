package gr.cognitera.util.jdbc;


/*
 * 
 * This is the connection mode where each statement is executed in its own transaction
 * and so we have to explicitly open a transaction (e.g. with BEGIN TRAN) when we wish
 * to do so.
 *
 * cf. http://mperdikeas.github.io/sql-concepts.html
 */

public abstract class AutocommitExplicitTransaction<T> implements ITransaction<T> {

    @Override
    public TransactionTypology getTypology() {
        return TransactionTypology.AUTOCOMMIT_EXPLICIT;
    }

}
