package gr.cognitera.util.jdbc;

import java.util.List;
import java.util.ArrayList;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;

import org.apache.log4j.Logger;
import org.apache.commons.dbutils.DbUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.base.StandardSystemProperty;

import gr.cognitera.util.base.StringUtil;
import gr.cognitera.util.base.Holder;

public class AbstractDALUtils {

    private AbstractDALUtils() {}

    protected final static void commit(final Logger logger, final Connection conn) {
        try {
            if (conn!=null) {
                final boolean autoCommit = (boolean) conn.getAutoCommit();
                if (!autoCommit) // see javadoc on Connection#commit and note-1. 
                    conn.commit();
                else {
                    logger.warn(String.format("Unnecessary commit in:%n---%%<-------------%n%s%n------------->%%---%n"
                                              , StringUtil.stringify(Thread.currentThread().getStackTrace())));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void commitAccordingToTypology(final Logger logger, final ITransactionDescribable trans, final Connection conn) {
        switch (trans.getTypology()) {
        case CHAINED_IMPLICIT:
            AbstractDALUtils.commit(logger, conn);
            logger.info(String.format("commited trans [%s] in %s mode"
                                      , AbstractDALUtils.getShortDescription(trans)
                                      , trans.getTypology()));
            break;
        case AUTOCOMMIT_EXPLICIT:
            ManualTransactionHelper.commit(conn);
            logger.info(String.format("commited trans [%s] in %s mode"
                                      , AbstractDALUtils.getShortDescription(trans)
                                      , trans.getTypology()));            
            final boolean IS_IT_NECESSARY_TO_COMMIT_AFTER_AN_EXPLICIT_COMMIT = false; // if set to [true] you get the "Unnecessary commit" warning (still works, just a bit superfluous - even though it might just feel safer ...)
            if (IS_IT_NECESSARY_TO_COMMIT_AFTER_AN_EXPLICIT_COMMIT)
                AbstractDALUtils.commit(logger, conn);  
            break;
        default:
            throw new RuntimeException(String.format("unhandled typology: %s", trans.getTypology()));
        }
    }

    

    protected static final void rollback(Logger logger, Connection conn) {
        try {
            if (conn!=null) {
                final boolean autoCommit = (boolean) conn.getAutoCommit();
                if (!autoCommit)
                    DbUtils.rollback(conn);
                else {
                    logger.warn(String.format("Unnecessary rollback in:%n---%%<-------------%n%s%n------------->%%---%n"
                                              , StringUtil.stringify(Thread.currentThread().getStackTrace())));
                    // note-1: do nothing as per the javadoc on Connection#rollback, see also: https://stackoverflow.com/q/44310592/274677
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean throwableSignifiesDeadlock(final Throwable t
                                                      , final Integer errorCodeForDeadlock
                                                      , final String sqlStateForDeadlock) {
        Assert.assertTrue( (errorCodeForDeadlock==null) || (sqlStateForDeadlock==null) );
        if (!(t instanceof SQLException))
            return false;
        else {
            final SQLException s = (SQLException) t;
            if ((errorCodeForDeadlock!=null) && (s.getErrorCode() == errorCodeForDeadlock))
                return true;
            if ((sqlStateForDeadlock!=null) && (s.getSQLState() == sqlStateForDeadlock))
                return true;
            return false;
        }
    }

    private static String messageForUnrecognizedError(final SQLException se
                                                      , final Integer errorCodeForDeadlock
                                                      , final String sqlStateForDeadlock) {
        Assert.assertTrue( (errorCodeForDeadlock==null) || (sqlStateForDeadlock==null) );
        if (errorCodeForDeadlock!=null)
            return (String.format("SQL error code reported by server was: [%d] which is different than"
                                  +" the error code that was expected to be used to signify deadlocks (%d)"
                                  , se.getErrorCode()
                                  , errorCodeForDeadlock));
        else
            return (String.format("SQL State reported by server was: [%s] which is different than"
                                  +" the SQL State that was expected to be used to signify deadlocks (%s)"
                                  , se.getSQLState()
                                  , sqlStateForDeadlock));
            
    }
    
    protected static void handleThrowable(final Logger logger
                                 , final Connection conn
                                 , final List<Throwable> throwables
                                 , final Throwable t
                                 , final ITransactionDescribable trans
                                 , final Holder<Boolean> connectionAlreadyClosed
                                 , final long msBegin
                                 , final int numOfTries
                                 , final int millisToWaitAfterDeadlock
                                 , final int maxNumberOfDeadlockRetries
                                 , final Integer errorCodeForDeadlock
                                 , final String sqlStateForDeadlock
                                        ) {
        Assert.assertTrue( (errorCodeForDeadlock==null) || (sqlStateForDeadlock==null) );
        if  ((t instanceof ApplicationInducedRetry)
             ||
             throwableSignifiesDeadlock(t, errorCodeForDeadlock, sqlStateForDeadlock)
             ||
             (SQLExceptionUtil.sqlExceptionIsPresentInTheStackWithRightError(logger, t, errorCodeForDeadlock, sqlStateForDeadlock))) {
            throwables.add(t);
            if (numOfTries>maxNumberOfDeadlockRetries) { // DeadLock Handling Attempt by means of Retries Eventual Failure
                final long msEnd = System.currentTimeMillis();
                AbstractDALUtils.logDeadlockEventualFailureAndThrow(logger, trans, numOfTries, msEnd - msBegin, throwables);
            } else {
                logger.debug(String.format("handleThrowable(): closing connection for trans: [%s], try #: [%d] due to throwable of class: [%s]%s (this is not the end, we'll re-try this transaction another %d times)"
                                           , AbstractDALUtils.getShortDescription(trans)
                                           , numOfTries
                                           , t.getClass().getName()
                                           , (t instanceof SQLException)?
                                           String.format(" (SQL error code = %d) (SQL state = '%s')"
                                                         , ((SQLException) t).getErrorCode()
                                                         , ((SQLException) t).getSQLState())
                                           :""
                                           , maxNumberOfDeadlockRetries-numOfTries+1));
                AbstractDALUtils.logWarnings(logger, trans, conn);
                DbUtils.closeQuietly(conn); // since we are going to wait, we are going to close the connection right away; otherwise we'd have to wait till the "finally" block
                logger.debug(String.format("handleThrowable(): connection closed, we're now going to wait for %d ms before retrying the transaction [%s]"
                                           , millisToWaitAfterDeadlock
                                           , AbstractDALUtils.getShortDescription(trans)));
                connectionAlreadyClosed.set(true);
                try {
                    TimeUnit.MILLISECONDS.sleep(millisToWaitAfterDeadlock);
                } catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
            }
        } else {
            logger.error(String.format("While executing transaction [%s], encountered exception of class [%s] (msg: %s), "
                                       +" whose class and / or entire trace was not anticipated by the transaction execution"
                                       +" machinery while executing transaction: %s"
                                       , AbstractDALUtils.getShortDescription(trans)
                                       , t.getClass().getName()
                                       , t.getMessage()==null?"NULL":t.getMessage().trim()
                                       , trans.getDescription()));
            logger.error(String.format("Entire stack trace was %s"
                                       , Throwables.getStackTraceAsString(t)));
            if (t instanceof SQLException) {
                logger.error(messageForUnrecognizedError((SQLException) t, errorCodeForDeadlock, sqlStateForDeadlock));
                logger.error(SQLExceptionUtil.stringifyEntireChain2((SQLException) t));
            }
            throw new RuntimeException(t);
        }
    }

    protected static final void setupTransactionAccordingToTypology(final ITransactionDescribable trans, final Connection conn) throws SQLException {
        switch (trans.getTypology()) {
        case CHAINED_IMPLICIT:
            conn.setAutoCommit(false);
            break;
        case AUTOCOMMIT_EXPLICIT:
            conn.setAutoCommit(true);
            ManualTransactionHelper.begin(conn);
            break;
        default:
            throw new RuntimeException(String.format("unhandled typology: %s", trans.getTypology()));
        }
    }

    protected static void rollbackAccordingToTypology(final Logger logger, final ITransactionDescribable trans, final Connection conn) {
        if (conn!=null) {
            switch (trans.getTypology()) {
            case CHAINED_IMPLICIT:
                rollback(logger, conn);
                logger.info(String.format("rolledbacked trans [%s] in %s mode"
                                          , AbstractDALUtils.getShortDescription(trans)
                                          , trans.getTypology()));
                break;
            case AUTOCOMMIT_EXPLICIT:
                ManualTransactionHelper.rollback(conn);
                logger.info(String.format("rolledbacked trans [%s] in %s mode"
                                          , AbstractDALUtils.getShortDescription(trans)
                                          , trans.getTypology()));            
                final boolean IS_IT_NECESSARY_TO_ROLLBACK_AFTER_AN_EXPLICIT_ROLLBACK = false; // if set to [true] you get the "Unnecessary rollback" warning from AbstractDAL (still works, just a bit superfluous)
                if (IS_IT_NECESSARY_TO_ROLLBACK_AFTER_AN_EXPLICIT_ROLLBACK)
                    rollback(logger, conn); 
                break;
            default:
                throw new RuntimeException(String.format("unhandled typology: %s", trans.getTypology()));
            }
        }
    }    


    private static int howMany(final SQLWarning warning) {
        int rv = 1;
        SQLWarning next = warning.getNextWarning();
        while (next!=null) {
            rv++;
            next = next.getNextWarning();
        }
        return rv;
    }

    public static void logWarnings(final Logger logger, final ITransactionDescribable trans, final Connection conn) {
        if (conn!=null)
        try {
            SQLWarning warning = conn.getWarnings();
            if (warning!=null) {
                final int howMany = howMany(warning);
                Assert.assertTrue(howMany>0);
                List<String> rv = new ArrayList<>();
                rv.add(String.format("Encountered [%d] warnings while executing [%s], chained in the following manner:"
                                     , howMany(warning)
                                     , trans.getDescription()));
                int i = 1;
                while (warning!=null) {
                    rv.add(String.format("warning [%d] of [%d]: error code = [%d], SQL state = [%s], message = [%s]"
                                         , i++
                                         , howMany
                                         , warning.getErrorCode()
                                         , warning.getSQLState()
                                         , warning.getMessage()));
                    warning = warning.getNextWarning();
                }
                logger.warn(Joiner.on(StandardSystemProperty.LINE_SEPARATOR.value()).join(rv));
            }
        } catch (SQLException e) {
            logger.error(String.format("Suppressing the following %s when trying to examine the connection warnings upon closure of transaction [%s]:\n"+
                                       "%s\n"+
                                       "<--- end of suppressed exception trace that occurred while trying to examine connection warnings associated with transaction [%s]"
                                       , e.getClass().getName()
                                       , trans.getDescription()
                                       , Throwables.getStackTraceAsString(e)
                                       , trans.getDescription()));
        }
    }


    public static String descriptionOfThrowables(List<Throwable> throwables) {
        List<String> classNames = new ArrayList<>();
        List<String> messages = new ArrayList<>();
        int i = 1;
        for (Throwable t: throwables) {
            classNames.add(t.getClass().getName());
            messages.add(String.format("[message %d of %d]: %s"
                                       , i++
                                       , throwables.size()
                                       , t.getMessage()).trim());
        }
        return String.format("Throwable classes were: %s. Throwable messages were: %s"
                             , Joiner.on(", ").join(classNames)
                             , Joiner.on(", ").join(messages));
    }

    public static void logDeadlockEventuallyHandled(final Logger logger, final ITransactionDescribable trans, final int numOfTries, final List<Throwable> throwables) {
        if (numOfTries > 1) {         // DeadLock Handled By Means of Retries
            logger.debug(String.format("DLHBMR-%d: transaction [%s] succeedeth on try # %d (after %d retries) due to deadlock that was handled by application logic by means of retries (no action needs to be taken in response to this message). Description of Throwables that induced the retries follow: %s"
                                       , numOfTries
                                       , trans.getDescription()
                                       , numOfTries                                               
                                       , numOfTries-1
                                       , descriptionOfThrowables(throwables)));
        }
    }

    public static void logDeadlockEventualFailureAndThrow(final Logger logger, final ITransactionDescribable trans, final int numOfTries, final long timeSpanMS, final List<Throwable> throwables) {
        final String MSG = String.format("DLHAREF: giving up after trying to execute transaction [%s] %d times (%d retries) over a span of %d millis. Description of Throwables that induced the retries follow: %s"
                                         , trans.getDescription()
                                         , numOfTries
                                         , numOfTries-1
                                         , timeSpanMS
                                         , descriptionOfThrowables(throwables));
        logger.debug(MSG);
        throw new MaxNumberOfDeadlockRetriesExceeded(MSG);
    }


    public static String getShortDescription(final ITransactionDescribable trans) {
        final int MAX_CHAR=70;
        final String descr = trans.getDescription();
        int maxLength = (descr.length() < MAX_CHAR)?descr.length():MAX_CHAR;
        return descr.substring(0, maxLength);
    }

    public static void logTransactionConclusion(final Logger logger, ITransactionDescribable trans, final int numOfTries, final long msBegin, final List<Throwable> throwables) {
        final long msEnd = System.currentTimeMillis();
        /* We say "finishing execution" (instead of e.g. "was executed") because, at this point in time, the transaction is not
           commited yet. Commit will happen immediately afterwards but there's still a slight chance that something might go awry.
        */
        logger.info(String.format("%s-%d: transaction [%s] finishing execution on try [%d] after a total of [%d] ms with %s"
                                  , "TRES" // TRansaction Execution Statistics
                                  , numOfTries
                                  , getShortDescription(trans)
                                  , numOfTries
                                  , msEnd - msBegin
                                  , throwables.isEmpty()
                                  ?"no throwables"
                                  :String.format("a total of [%d] throwables: {%s}"
                                                 , throwables.size()
                                                 , descriptionOfThrowables(throwables))));
    }
}
