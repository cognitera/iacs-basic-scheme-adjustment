package gr.cognitera.iacs.basic_scheme_adjustment;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.Map;

import java.sql.Connection; import java.sql.SQLException; import java.sql.PreparedStatement; import java.sql.CallableStatement; import java.sql.ResultSet; import java.sql.Timestamp; import java.sql.Statement; import java.sql.ResultSetMetaData; import java.sql.Types;

import org.junit.Assert;


import org.apache.commons.dbutils.DbUtils;

import com.google.common.base.Joiner;
import com.google.common.io.CharStreams;

import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.ST;


import gr.cognitera.util.base.Util;
import gr.cognitera.util.base.RandomStringGenerator;
import gr.cognitera.util.base.TypedExceptionThrower;
import gr.cognitera.util.base.SCAUtils;

import gr.cognitera.util.jdbc.DBLoggingUtil;
import gr.cognitera.util.jdbc.JDBCUtil;
import gr.cognitera.util.jdbc.ResultSetHelper;
import gr.cognitera.util.jdbc.ResultSetHelperUtil;
import gr.cognitera.util.jdbc.CustomFieldReader;
import gr.cognitera.util.jdbc.CustomFieldReaderUtil;
import gr.cognitera.util.jdbc.ResultSetHelper;
import gr.cognitera.util.base.SCAUtils;


class RightsRetrieve {}

public class DBRightsTable {

    final static Class<?> klass;
    final static Logger logger;
    static {
        klass = DBRightsTable.class;
        logger = Logger.INSTANCE;
    }

    public static List<Right> readAllRights(final Connection conn ) throws SQLException {
        final Class klass = DBRightsTable.class; 
        PreparedStatement ps   = null;
        ResultSet         rs   = null;
        try {
            /*           final URL resource = DBApplication.class.getResource(String.format("./%s.sql", klass.getSimpleName()));
            final STGroupFile group = new STGroupFile(resource, StandardCharsets.UTF_8.name(), '<', '>');
            final ST st = group.getInstanceOf("application");
            Assert.assertNotNull(st);
            //            st.add("appId"     , appId);
            final String SQL = st.render();
            logger.debug(String.format("SQL query read as [%s]\n", SQL));*/
            final String SQL = "SELECT"
                +" x.ID               AS id, "
                +" x.BPE_ID           AS internal_id, "
                +" x.BPE_GROUP_TYPE   AS source, "
                +" x.BPE_TYPE         AS type, "
                +" x.BPEQTY           AS quantity, "
                +" x.BPEUP            AS unit_value "
                +" FROM GAEE2021.EDETEDEAEEBPE x"
                //    +" WHERE mod(id, 1000)=0" // TODO
                ;
            ps = conn.prepareStatement(SQL);
            //            ps.setInt(1, appId);
            rs = ps.executeQuery();
            List<Right> rv = new ArrayList<>();
            int i = 0;
            final Map<String, CustomFieldReader<?>> fieldOverrides =
                CustomFieldReaderUtil.prepareFieldOverrides(Right.class
                                                            , new RightSourceFieldReader()
                                                            , new RightTypeFieldReader());
            while (rs.next()) {
                logger.trace(".");
                final Right right = ResultSetHelper.readObject((org.apache.log4j.Logger)null
                                                               , rs
                                                               , Right.class
                                                               , fieldOverrides
                                                               , new LinkedHashSet<String>());

                rv.add(right);
                i++;
            }
            logger.debug("\n\nWent through %d rows\n", i);
            return rv;
        }
     /*catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
            } */ finally {
            DbUtils.closeQuietly((Connection) null, ps, rs);
        }
    }
}
