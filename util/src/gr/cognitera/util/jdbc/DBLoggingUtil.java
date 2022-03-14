package gr.cognitera.util.jdbc;

import org.apache.log4j.Logger;

public final class DBLoggingUtil {

    private DBLoggingUtil() {}

    private static final String START_OF_QUERY = "------- start of query ----------------";
    private static final String END_OF_QUERY   = "------- end of query ----------------";
    
    public static void debugSQL(final Logger logger, final String sql) {
        logger.debug(String.format("SQL query read as follows:\n%s%s%s"
                                   , START_OF_QUERY
                                   , sql
                                   , END_OF_QUERY));
    }
    
    


}
