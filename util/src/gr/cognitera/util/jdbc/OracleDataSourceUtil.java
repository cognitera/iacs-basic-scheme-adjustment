package gr.cognitera.util.jdbc;

import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

public class OracleDataSourceUtil {



    // "jdbc:oracle:thin:@myhost:1521:orcl", "scott", "tiger"


    public static DataSource getDataSource(final String ip,
                                           final int    port,
                                           final String db,
                                           final String user,
                                           final String pwd,
                                           final int    maxActiveConnections,
                                           final int    maxIdleConnections) {
        final BasicDataSource dS = new BasicDataSource();
        final String URL = String.format("jdbc:oracle:thin:@%s:%d:%s", ip, port, db);
        dS.setDriverClassName("oracle.jdbc.OracleDriver");
        dS.setUsername(user);
        dS.setPassword(pwd);
        dS.setUrl(URL);
        dS.setMaxActive(maxActiveConnections);
        dS.setMaxIdle(maxIdleConnections);
        dS.setInitialSize(1);
        dS.setValidationQuery("select 1 from dual");
        return dS;
    }

}
