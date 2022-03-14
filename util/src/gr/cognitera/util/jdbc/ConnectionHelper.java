package gr.cognitera.util.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import org.apache.commons.dbutils.DbUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class ConnectionHelper {

    private ConnectionHelper() {
    }

    public static void lockTableInExclusiveMode(final Connection conn, final String tableName) throws SQLException {
        lockTable(conn, tableName, LOCK_MODE.EXCLUSIVE);
    }

    public static void lockTableInShareMode(final Connection conn, final String tableName) throws SQLException {
        lockTable(conn, tableName, LOCK_MODE.SHARE);
    }

    @SuppressFBWarnings(value="SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")    
    private static void lockTable(final Connection conn, final String tableName, final LOCK_MODE mode) throws SQLException {
        Statement stmt = null;
        try {
            // there is absolutely no need and no benefit in using a PreparedStatement in this case
            final String SQL = String.format("LOCK TABLE %s IN %s MODE"
                                             , tableName
                                             , mode.value);
            stmt = conn.createStatement();
            stmt.execute(SQL);
        } finally {
            DbUtils.closeQuietly((Connection) null, stmt, (ResultSet) null);
        }
    }    
}

