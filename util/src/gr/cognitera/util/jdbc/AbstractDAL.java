package gr.cognitera.util.jdbc;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Connection;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import org.junit.Assert;

import org.apache.log4j.Logger;

import org.apache.commons.dbutils.DbUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;


import gr.cognitera.util.base.StringUtil;
import gr.cognitera.util.base.Holder;
import gr.cognitera.util.base.Unit;



public abstract class AbstractDAL {

    protected abstract DataSource getDataSource();

    private final boolean errorCodeForDeadlockIsImplemented() {
        try {
            errorCodeForDeadlock();
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    private final boolean sqlStateForDeadlockIsImplemented() {
        try {
            sqlStateForDeadlock();
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    private void assertNotBothAreImplemented() {
        final boolean v1 = errorCodeForDeadlockIsImplemented();
        final boolean v2 = sqlStateForDeadlockIsImplemented();
        Assert.assertFalse("you can't supply implementations for both; only for, at most, one "+
                           "of these methods: [errorCodeForDeadlock] and [sqlStateForDeadlockIsImplemented]"
                           , v1 && v2);
    }
    
    private final boolean criterionToDetectDeadlockIsErrorCode() {
        assertNotBothAreImplemented();
        final boolean v1 = errorCodeForDeadlockIsImplemented();
        final boolean v2 = sqlStateForDeadlockIsImplemented();
        Assert.assertTrue("method [criterionToDetectDeadlockIsErrorCode] is only called from inside [executeTransaction]; "+
                          "As such, one of these two methods must be implemented: "+
                          "[errorCodeForDeadlock] and [sqlStateForDeadlockIsImplemented]"
                          , v1 || v2);
        return v1;
    }

    public AbstractDAL() {
        assertNotBothAreImplemented();
    }



    /**
     * Returns the error code used to signify deadlock conditions in the particular RDBMS you work against
     * <p>
     * This method only comes into play if you use the transaction execution machinery offered by this class
     * (i.e. method {@link #executeTransaction executeTransaction})
     * In such a case you <i>must</i> override this method in your concrete subclass (e.g see {@link SybaseAbstractDAL}).
     * 
     * @return the error code used to signify deadlock in the particular RDBMS we're working against
     *
     */
    protected int errorCodeForDeadlock() {
        throw new UnsupportedOperationException(String.format("you must override this in your subclass if you rely on the transaction execution machinery provided by %s"
                                                              , AbstractDAL.class.getName()));
    }

    /**
     * Returns the SQL State used to signify deadlock conditions in the particular RDBMS you work against
     * <p>
     * This method only comes into play if you use the transaction execution machinery offered by this class
     * (i.e. method {@link #executeTransaction executeTransaction})
     * In such a case you <i>must</i> override this method in your concrete subclass (e.g see {@link PostgreSQLAbstractDAL}).
     * 
     * @return the 5 char wide SQLState (according to the SQL standard) used to signify deadlock in the particular RDBMS we're working against
     *
     */
    protected String sqlStateForDeadlock() {
        throw new UnsupportedOperationException(String.format("you must override this in your subclass if you rely on the transaction execution machinery provided by %s"
                                                              , AbstractDAL.class.getName()));
    }    

    /**
     * Returns the maximum number of retries in the event of a deadlock.
     * <p>
     * Set to 0 to never retry in case of deadlocks and thus to attempt to execute each transaction only once.
     * <p>
     * This method only comes into play if you use the transaction execution machinery offered by this class
     * (i.e. method {@link #executeTransaction executeTransaction})
     * In such a case you <i>must</i> override this method in your concrete subclass (e.g see {@link SybaseAbstractDAL}).
     * 
     * @return the maximum number of deadlock retries (0 for no retries ever)
     *
     */
    protected int maxNumberOfDeadlockRetries() {
        throw new UnsupportedOperationException(String.format("you must override this in your subclass if you rely on the transaction execution machinery provided by %s"
                                                              , AbstractDAL.class.getName()));
    }

    private Random random = new Random();
    protected  int millisToWaitAfterDeadlock(int numOfTry) {
        return numOfTry*numOfTry*15+numOfTry*80+(random.nextInt()%100);
    }


    /**
     * Supplies the {@code Logger} to use for this class.
     * <p>
     * You <i>may</i> wish to override this method and supply your own {@code Logger} object
     * (instead of the default one) to ensure that your logging messages end-up in
     * a dedicated file for your application.
     *
     * @return the {@code Logger} to use.
     *
     */
    protected Logger getLogger() {
        return Logger.getLogger(AbstractDAL.class);
    }

    protected int getTransactionIsolationLevel() {
        return Connection.TRANSACTION_READ_COMMITTED;
    }


    /**
     * Returns the autocommit mode for connections obtained using this class
     * <p>
     * This method only comes into play if you do <i>not</i> use the transaction execution machinery
     * offered by this class. I.e. if you do not use method
     *  {@link #executeTransaction executeTransaction}.
     * If, OTOH, you <i>do</i> use that method, then the value returned by this method is irrelevent
     * and does not come into play as the autocommit level is effectively set
     * by {@link AbstractDALUtils#setupTransactionAccordingToTypology setupTransactionAccordingToTypology}.
     *
     * @return the (initial) autocommit mode for connections obtained using this class
     *
     */
    protected boolean getAutoCommit() {
        return false; // false is the SQL 92 chained transaction mode, true is the Sybase ASE unchained transaction mode
    }

    protected final Connection getConnection(int transactionIsolationLevel) throws SQLException {
        Logger logger = getLogger();
        logger.debug("calling DataSource#getConnection() ...");
        long l1 = System.currentTimeMillis();
        Connection conn = getDataSource().getConnection();
        long l2 = System.currentTimeMillis();
        logger.debug(String.format(" ... DataSource#getConnection() returned after [%d] ms"
                                   , l2-l1));
        conn.setAutoCommit(getAutoCommit());  
        conn.setTransactionIsolation(transactionIsolationLevel);
        return conn;
    }

    protected final Connection getConnection() throws SQLException {
        return getConnection(getTransactionIsolationLevel());
    }


    protected final void executeTransactionReturningVoid(final ITransactionReturningVoid trans) {
        Unit mostlyIgnored = executeTransaction(new TransactionReturningVoidToUnitTypeAdapter(trans));
        Assert.assertNotNull(mostlyIgnored);
    }


    
    protected final <T> T executeTransaction(final ITransaction<T> trans) {
        final Logger logger = getLogger();
        logger.info(String.format("executeTransaction() entered for transaction: [%s]"
                                  , AbstractDALUtils.getShortDescription(trans)));

        final boolean v1 = errorCodeForDeadlockIsImplemented();
        final boolean v2 = sqlStateForDeadlockIsImplemented();

        Assert.assertTrue("to use [executeTransaction] you have to implement one of the two methods: "+
                          "[errorCodeForDeadlock] or [sqlStateForDeadlock]"
                          , v1 || v2);
        Assert.assertFalse(v1 && v2);
        int numOfTries = 0;
        final long msBegin = System.currentTimeMillis();
        final List<Throwable> throwables = new ArrayList<>();
        while (true) {
            Holder<Boolean> connectionAlreadyClosed = new Holder<>(false);
            Connection conn = null;
            try {
                numOfTries ++;
                logger.debug(String.format("Trans: [%s]; try: #[%d] asking for connection..."
                                           , AbstractDALUtils.getShortDescription(trans)
                                           , numOfTries));                
                conn = getConnection();
                logger.debug(String.format("Trans: [%s]; try: #[%d] connection obtained."
                                           , AbstractDALUtils.getShortDescription(trans)
                                           , numOfTries));                
                AbstractDALUtils.setupTransactionAccordingToTypology(trans, conn);
                T t = trans.doTrans(conn);
                AbstractDALUtils.logDeadlockEventuallyHandled(logger, trans, numOfTries, throwables);
                AbstractDALUtils.logTransactionConclusion(logger, trans, numOfTries, msBegin, throwables);
                return t; // corr-1518814402
            } catch (Throwable t) {
                AbstractDALUtils.rollbackAccordingToTypology(logger, trans, conn);
                AbstractDALUtils.handleThrowable(logger, conn, throwables, t, trans, connectionAlreadyClosed, msBegin
                                , numOfTries, millisToWaitAfterDeadlock(numOfTries), maxNumberOfDeadlockRetries()
                                                 , this.criterionToDetectDeadlockIsErrorCode()?this.errorCodeForDeadlock():null
                                                 , this.criterionToDetectDeadlockIsErrorCode()?null:this.sqlStateForDeadlock());
            } finally {
                if (conn!=null) { // conn==null is only possible if getConnection() failed. In such a case there's nothing to do.
                    /*  There are three possible ways to come to this point:
                        I.   The transaction succeeded and returned a value (corr-1518814402).
                        II.  The transaction threw an exception so it was rollbacked and the [handleThrowable] closed
                             the connection and otherwise suppressed the exception because it was of a deadlock type AND we hadn't yet 
                             reached the maximum allowable number of deadlock-induced retries
                        III. The transaction threw an exception so it was rollbacked and the [handleThrowable] threw an exception
                             EITHER because the exception was not of a deadlock type OR because we have reached the maximum allowable 
                             number of deadlock-induced retries. In this case the [handleThrowable] method doesn't close the connection.

                        In case I we need to commit and close the connection
                        In case II the connection has already been rollbacked and has already been closed so there's nothing else to do here.
                        In case III we need to close the connection. In the below code we also commit in this case but that doesn't hurt at all
                           since we've already rollbacked.
                     */
                        
                    if (!connectionAlreadyClosed.get()) {
                        // cases I and III
                        try {
                            AbstractDALUtils.commitAccordingToTypology(logger, trans, conn);
                        } finally {
                            AbstractDALUtils.logWarnings(logger, trans, conn);
                            logger.debug(String.format("Trans: [%s]; try: #[%d] closing connection..."
                                                       , AbstractDALUtils.getShortDescription(trans)
                                                       , numOfTries));
                            DbUtils.closeQuietly(conn);
                            logger.debug(String.format("Trans: [%s]; try: #[%d] connection closed."
                                                       , AbstractDALUtils.getShortDescription(trans)
                                                       , numOfTries));
                        }
                    } else
                        ; // case II (nothing to be done)
                }
            }
        }
    }
}
