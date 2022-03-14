package gr.cognitera.util.jdbc;

import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

public abstract class AbstractDataSourceUtil {


    protected abstract String getDriverClassName();
    /*
     * Some values I have used:
     * 
     * Database              | Driver                              | example dURL
     * -------------------------------------------------------------------------------------------------------------------
     * PostgreSQL 9.x        | org.postgresql.Driver               | jdbc:postgresql://localhost:5432/RegTAP
     * Sybase ASE 15.7       | net.sourceforge.jtds.jdbc.Driver    | jdbc:jtds:sybase://131.142.197.150:2545/axafusers
     * Sybase ASE 15.7       | com.sybase.jdbc4.jdbc.SybDriver     | jdbc:sybase:Tds:131.142.197.150:2545/axafusers
     */

    protected int    getConnectionPoolInitialSize() { return 1; }
    protected String getValidationQuery          () { return "SELECT 1"; }

    public DataSource getDataSource(final String dbURL, final String user, final String pwd, final int maxActiveConnections) {
        final int MAX_IDLE_DEFAULT = 0;
        return getDataSource(dbURL, user, pwd, maxActiveConnections, MAX_IDLE_DEFAULT);
    }
    
    public DataSource getDataSource(final String dbURL, final String user, final String pwd, final int maxActiveConnections, int maxIdleConnections) {
        final BasicDataSource dS = new BasicDataSource();
        dS.setDriverClassName(getDriverClassName());
        dS.setUsername(user);
        dS.setPassword(pwd);
        dS.setUrl(dbURL);
        dS.setMaxActive(maxActiveConnections);
        dS.setMaxIdle(maxIdleConnections);
        dS.setInitialSize(getConnectionPoolInitialSize());
        dS.setValidationQuery(getValidationQuery());
        return dS;
    }

}
