package gr.cognitera.util.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface ITransaction<T> extends ITransactionDescribable {

    T   doTrans(final Connection conn) throws SQLException;

}
