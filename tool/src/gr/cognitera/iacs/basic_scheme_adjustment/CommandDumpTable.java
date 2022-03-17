package gr.cognitera.iacs.basic_scheme_adjustment;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.junit.Assert;
import org.apache.http.Header;
import org.apache.commons.dbutils.DbUtils;



import com.google.common.io.Files;
import com.google.common.base.Stopwatch;


import gr.cognitera.util.gson.GsonHelper;
import gr.cognitera.util.jdbc.OracleDataSourceUtil;

public class CommandDumpTable {
    private static final String EUR = "\u20ac";
    final static Logger logger = Logger.INSTANCE;

    public static void exec(final String configFPath) {
        logger.debug(String.format("Configuration file is: [%s]\n", configFPath));
        try {
            final String json = Files.asCharSource(new File(configFPath), StandardCharsets.UTF_8).read();
            final Config config = GsonHelper.fromJson(json, Config.class);
            final DBConfig dbConfig = config.db; 
            final DataSource ds = OracleDataSourceUtil.getDataSource(dbConfig.ip
                                                                     , dbConfig.port
                                                                     , dbConfig.dbname
                                                                     , dbConfig.username
                                                                     , dbConfig.password
                                                                     , 1
                                                                     , 0);
            final Connection conn = ds.getConnection();
            logger.info("Reading rights from %s:%d database %s...\n"
                        , dbConfig.ip
                        , dbConfig.port
                        , dbConfig.dbname);

            final Stopwatch stopwatch = Stopwatch.createStarted();
            List<Right> rights = DBRightsTable.readAllRights(conn);
            DbUtils.closeQuietly(conn);
            stopwatch.stop();
            logger.info("%d rights read in %d seconds.\n"
                        , rights.size()
                        , stopwatch.elapsed(TimeUnit.SECONDS));
            logger.info("Acronyms to keep in mind:\n");
            logger.info("AR: All Rights\n");
            logger.info("RBRV: Rights Below Regional Value\n");
            logger.info("RARV: Rights Above Regional Value\n");
            final RightStats stats1 = Right.computeStats(rights);
            logger.info("(before adjustment) | AR: %d records %.2f rights %.2f%s\n"
                        , stats1.numOfRecords
                        , stats1.numOfRights
                        , stats1.valueOfRights
                        , EUR);
            logger.info("Proceeding to adjust rights...\n");
            rights = AdjustRights.doWork(config.regional_values, rights, RightType.PASTURE);
            rights = AdjustRights.doWork(config.regional_values, rights, RightType.ARABLE);
            rights = AdjustRights.doWork(config.regional_values, rights, RightType.PERMACROP);
            final RightStats stats2 = Right.computeStats(rights);            
            logger.info("(after adjustment) | AR: %d records %.2f rights %.2f%s\n"
                        , stats2.numOfRecords
                        , stats2.numOfRights
                        , stats2.valueOfRights
                        , EUR);
        } catch (IOException e) {
            Assert.fail(String.format("DB configuration file [%s] not found", configFPath));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

