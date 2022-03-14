package gr.cognitera.util.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Assert;

import org.apache.commons.dbutils.DbUtils;

public final class ManualTransactionHelper {

    private ManualTransactionHelper() {
    }


    private static final String MSG = "The use case for employing methods in this class is the need to nest transactions because one of the statements (usually a SP call), has to be executed in auto-commit mode and that would inadvertedly result in commiting previously executed statements in the same transaction (or the result of the SP itelf) and this would thwart our ability to group a number of statements in a single transaction. By nesting the transaction what we are achieving is to effectively *override* the autocommit nature of a SP call (both with respect to the commit of its own changes and with respect to the implicit commit of the changes of any prior statements) and force it to behave as part of a single transaction without any commits (implicit or otherwise) until the explicitly opened nested transaction also closes with an explicit 'COMMIT TRANSACTION' message (or a ROLLBACK). However you are apparently nesting a transaction on a connection that's not on autocommit mode. This makes no sense (or at least is a use case I haven't yet encountered nor thought about).";
    public static void begin(final Connection conn) {
        Statement stmt = null;
        try {
            Assert.assertTrue(MSG, conn.getAutoCommit());
            stmt = conn.createStatement();
            stmt.execute("BEGIN TRANSACTION");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly((Connection) null, stmt, (ResultSet) null);
        }
    }

    public static void rollback(final Connection conn) {
        Statement stmt = null;
        try {
            Assert.assertTrue(MSG, conn.getAutoCommit());
            stmt = conn.createStatement();
            stmt.execute("ROLLBACK TRANSACTION");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly((Connection) null, stmt, (ResultSet) null);
        }
    }

    public static void commit(final Connection conn) {
        Statement stmt = null;
        try {
            Assert.assertTrue(MSG, conn.getAutoCommit());
            stmt = conn.createStatement();
            stmt.execute("COMMIT TRANSACTION");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DbUtils.closeQuietly((Connection) null, stmt, (ResultSet) null);
        }
    }
}
