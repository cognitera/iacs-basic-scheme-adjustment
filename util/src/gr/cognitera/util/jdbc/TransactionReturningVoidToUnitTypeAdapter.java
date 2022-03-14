package gr.cognitera.util.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import gr.cognitera.util.base.Unit;

public class TransactionReturningVoidToUnitTypeAdapter implements ITransaction<Unit> {

    private final ITransactionReturningVoid originalTrans;

    public TransactionReturningVoidToUnitTypeAdapter(final ITransactionReturningVoid originalTrans) {
        this.originalTrans = originalTrans;
    }

    @Override
    public Unit doTrans(final Connection conn) throws SQLException {
        originalTrans.doTrans(conn);
        return Unit.THE_ONE_VALUE;

    }


    @Override
    public TransactionTypology getTypology() {
        return originalTrans.getTypology();
    }

    @Override
    public String getDescription() {
        return originalTrans.getDescription();
    }
}
