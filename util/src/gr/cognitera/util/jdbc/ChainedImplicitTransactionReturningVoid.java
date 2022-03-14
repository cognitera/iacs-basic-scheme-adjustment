package gr.cognitera.util.jdbc;

// see comment in ChainedImplicitTransaction.java
public abstract class ChainedImplicitTransactionReturningVoid implements ITransactionReturningVoid {

    @Override
    public TransactionTypology getTypology() {
        return TransactionTypology.CHAINED_IMPLICIT;
    }

}
