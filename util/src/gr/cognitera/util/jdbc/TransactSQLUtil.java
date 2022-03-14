package gr.cognitera.util.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import org.apache.commons.dbutils.DbUtils;


/**
 * These utilities have been tested against Sybase ASE 15.7 but in principle
 * should also work against any T-SQL server. 
 */
public class TransactSQLUtil {

    private TransactSQLUtil() {}

    public static void startTransaction(Connection conn) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute("BEGIN TRANSACTION");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly((Connection) null, stmt, (ResultSet) null);
        }
    }

    public static void rollbackTransaction(Connection conn) {
        Statement stmt = null;
        try {        
            stmt = conn.createStatement();
            stmt.execute("ROLLBACK TRANSACTION");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly((Connection) null, stmt, (ResultSet) null);
        }
    }
}
