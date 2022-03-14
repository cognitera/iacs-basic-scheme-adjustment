package gr.cognitera.util.jdbc;

import java.util.Map;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import javax.sql.DataSource;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Assert;

public class DataSourceLookupHelper {

    private static Logger _logger = Logger.getLogger(DataSourceLookupHelper.class);

    private DataSourceLookupHelper() {}



    public static DataSource lookupDataSource(final String name) {
        return lookupDataSource(name, _logger);
    }

    public static DataSource lookupDataSource(final String name, final Logger logger) {
        try {
            InitialContext cxt = new InitialContext();
            DataSource ds = (DataSource) cxt.lookup( name );
            return ds;
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
