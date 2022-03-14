package gr.cognitera.iacs.basic_scheme_adjustment;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.junit.Assert;
import org.apache.log4j.Logger;
import org.apache.http.Header;

import com.google.common.io.Files;



import gr.cognitera.util.gson.GsonHelper;
import gr.cognitera.util.jdbc.OracleDataSourceUtil;

public class CommandDumpTable {
    final static Logger logger = Logger.getLogger(CommandDumpTable.class);

    public static void exec(final String configFPath) {
        logger.info(String.format("Configuration file is: [%s]\n", configFPath));
        try {
            final String json = Files.asCharSource(new File(configFPath), StandardCharsets.UTF_8).read();
            final DBConfig dbConfig = GsonHelper.fromJson(json, DBConfig.class);
            final DataSource ds = OracleDataSourceUtil.getDataSource(dbConfig.ip
                                                                     , dbConfig.port
                                                                     , dbConfig.dbname
                                                                     , dbConfig.username
                                                                     , dbConfig.password
                                                                     , 1
                                                                     , 0);
            final Connection conn = ds.getConnection();
            DBRightsTable.doTran(conn, "foo");
        } catch (IOException e) {
            Assert.fail(String.format("DB configuration file [%s] not found", configFPath));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

