package gr.cognitera.util.jdbc;

import java.util.List;
import java.util.ArrayList;

import java.sql.SQLException;
import org.junit.Assert;
import org.apache.log4j.Logger;

import com.google.common.base.Joiner;

public final class SQLExceptionUtil {

    private SQLExceptionUtil() {}

    public static final String stringify(final SQLException e) {
        return String.format("Class [%s] # Message: [%s] # Error code: [%d] # SQL State: [%s]"
                             , e.getClass().getName()
                             , e.getMessage()
                             , e.getErrorCode()
                             , e.getSQLState());
    }

    public static final List<String> stringifyEntireChain(final SQLException e) {
        final List<String> rv = new ArrayList<>();
        SQLException p = e;
        while (p != null) {
            rv.add(stringify(p));
            p = p.getNextException();
        }
        return rv;
    }

    public static final String stringifyEntireChain2(final SQLException e) {
        final List<String> rv = new ArrayList<>();
        SQLException p = e;
        while (p != null) {
            rv.add(String.format("{%s}", stringify(p)));
            p = p.getNextException();
        }
        return String.format("[%d] %s instances. Entire chain is (from top level to causes): %s"
                             , rv.size()
                             , SQLException.class.getSimpleName()
                             , Joiner.on(", ").join(rv));
    }

    public static boolean sqlExceptionIsPresentInTheStackWithRightError(final Logger logger
                                                                        , final Throwable t
                                                                        , final Integer errorCodeForDeadlock
                                                                        , final String sqlStateForDeadlock) {
        Assert.assertTrue( (errorCodeForDeadlock==null) || (sqlStateForDeadlock==null) );
        Throwable p = t;
        int depth = 0;
        while (p != null) {
            if (p instanceof SQLException) {
                final SQLException s = (SQLException) p;
                final int actualErrorCode = s.getErrorCode();
                final String actualSQLState = s.getSQLState();
                if ((errorCodeForDeadlock!=null) && (actualErrorCode==errorCodeForDeadlock))
                    return true;
                else if ((sqlStateForDeadlock!=null) && actualSQLState.equals(sqlStateForDeadlock))
                    return true;
                else {
                    String discrepancy = null;
                    if (errorCodeForDeadlock!=null)
                        discrepancy = String.format("error code was [%d] and not [%d] as expected"
                                                    , actualErrorCode
                                                    , errorCodeForDeadlock);
                    else
                        discrepancy = String.format("SQL state was '%s' and not '%s' as expected"
                                                    , actualSQLState
                                                    , sqlStateForDeadlock);
                    logger.debug(String.format("Encountered instance of %s (actual class: %s) on "+
                                               "depth %d but %s"
                                               , SQLException.class.getSimpleName()
                                               , p.getClass().getName()
                                               , depth
                                               , discrepancy
                                               ));
                }
            }
            p = p.getCause();
            depth++;
        }
        return false;
    }
}
