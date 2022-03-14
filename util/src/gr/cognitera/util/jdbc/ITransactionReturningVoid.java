package gr.cognitera.util.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface ITransactionReturningVoid extends ITransactionDescribable {

    void doTrans(final Connection conn) throws SQLException;
    
}
