package gr.cognitera.util.jdbc;

// see comment in AutocommitExplicitTransaction.java
public abstract class AutocommitExplicitTransactionReturningVoid implements ITransactionReturningVoid {

    @Override
    public TransactionTypology getTypology() {
        return TransactionTypology.AUTOCOMMIT_EXPLICIT;
    }

}
