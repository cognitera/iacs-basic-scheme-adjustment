package gr.cognitera.util.jdbc;

import java.nio.charset.StandardCharsets;
import java.net.URL;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.Clob;
import java.sql.Blob;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.math.BigDecimal;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.apache.log4j.Logger;

import org.apache.commons.dbutils.DbUtils;

import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.ST;

import com.google.common.base.Throwables;
import com.google.gson.reflect.TypeToken;

import gr.cognitera.util.base.Util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class HLUtil {

    private HLUtil() {}

    public static int executeUpdateIntReturnRowsAffected(final Logger logger
                                                         , final Class<?> klass
                                                         , final String queryName
                                                         , final Connection conn
                                                         , final int x) throws SQLException {
        PreparedStatement ps   = null;
        try {
            final URL resource = klass.getResource(String.format("./%s.sql", klass.getSimpleName()));
            final STGroupFile group = new STGroupFile(resource, StandardCharsets.UTF_8.name(), '<', '>');
            final ST st = group.getInstanceOf(queryName);
            Assert.assertNotNull(st);
            final String SQL = st.render();
            logger.debug(String.format("SQL query read as [%s]\n", SQL));
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, x);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected;
        } finally {
            DbUtils.closeQuietly(ps);
        }
    }

    public static int deleteByIntKey(final Logger logger
                                      , final Connection conn
                                      , final String tableName
                                      , final String keyName
                                      , final int key) throws SQLException {
        PreparedStatement ps   = null;
        ResultSet         rs   = null;
        try {
            final String SQL = String.format("DELETE FROM %s WHERE %s = ?"
                                             , tableName
                                             , keyName
                                             , key);
            DBLoggingUtil.debugSQL(logger, SQL);
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, key);
            final int rowsAffected = ps.executeUpdate();
            return rowsAffected;
        } finally {
            DbUtils.closeQuietly((Connection) null, ps, rs);
        }
    }

    public static List<Integer> readAllKeysByInt(final Logger logger
                                                 , final Connection conn
                                                 , final String tableName
                                                 , final String keyName
                                                 , final int key
                                                 , final String columnName) throws SQLException {
        PreparedStatement ps   = null;
        ResultSet         rs   = null;
        try {
            final String SQL = String.format("SELECT %s FROM %s WHERE %s = ? ORDER BY %s"
                                             , columnName
                                             , tableName
                                             , keyName
                                             , columnName);
            DBLoggingUtil.debugSQL(logger, SQL);
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, key);
            rs = ps.executeQuery();
            List<Integer> rv = new ArrayList<>();
            while (rs.next()) {
                rv.add(ResultSetHelper.getInteger(rs, columnName));
            }
            return rv;
        }
        /*catch (IOException | URISyntaxException e) {
          throw new RuntimeException(e);
          } */ finally {
            DbUtils.closeQuietly((Connection) null, ps, rs);
        }
    }

    public static <T> List<T> readAllByInt(final Logger logger
                                           , final Class referenceClassForScript
                                           , final Class<T> klass
                                           , final String sqlName
                                           , final Connection conn
                                           , final int key
                                           , final boolean strict
                                           , final Map<String, CustomFieldReader<?>> fieldOverrides
                                           , final Map<String, String> field2Column) throws SQLException {
        PreparedStatement ps   = null;
        ResultSet         rs   = null;
        try {
            logger.debug(String.format("%s.readAllByInt(%s, %d)", HLUtil.class.getSimpleName(), sqlName, key));
            final URL resource = referenceClassForScript.getResource(String.format("./%s.sql"
                                                                                   , referenceClassForScript.getSimpleName()));
            final STGroupFile group = new STGroupFile(resource, StandardCharsets.UTF_8.name(), '<', '>');
            final ST st = group.getInstanceOf(sqlName);
            Assert.assertNotNull(st);
            final String SQL = st.render();
            DBLoggingUtil.debugSQL(logger, SQL);
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, key);
            rs = ps.executeQuery();
            List<T> rv = new ArrayList<>();
            while (rs.next()) {
                rv.add(ResultSetHelper.readObject(logger
                                                  , rs
                                                  , klass
                                                  , strict
                                                  , fieldOverrides
                                                  , field2Column));
            }
            return rv;
        }
        /*catch (IOException | URISyntaxException e) {
          throw new RuntimeException(e);
          } */ finally {
            DbUtils.closeQuietly((Connection) null, ps, rs);
        }
    }

    public static <T> T readByInt(final Logger logger
                                  , final Class referenceClassForScript
                                  , final Class<T> klass
                                  , final String sqlName
                                  , final Connection conn
                                  , final int key
                                  , final boolean strict
                                  , final Map<String, CustomFieldReader<?>> fieldOverrides
                                  , final Map<String, String> field2Column) throws SQLException {
        PreparedStatement ps   = null;
        ResultSet         rs   = null;
        try {
            logger.debug(String.format("%s.readByInt(%s, %d)", HLUtil.class.getSimpleName(), sqlName, key));
            final URL resource = referenceClassForScript.getResource(String.format("./%s.sql"
                                                                                   , referenceClassForScript.getSimpleName()));
            final STGroupFile group = new STGroupFile(resource, StandardCharsets.UTF_8.name(), '<', '>');
            final ST st = group.getInstanceOf(sqlName);
            Assert.assertNotNull(st);
            final String SQL = st.render();
            DBLoggingUtil.debugSQL(logger, SQL);
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, key);
            rs = ps.executeQuery();
            T rv = null;
            boolean beenHere = false;
            while (rs.next()) {
                if (beenHere)
                    Assert.fail(String.format("key=%d", key));
                beenHere = true;
                rv = ResultSetHelper.readObject(logger
                                                , rs
                                                , klass
                                                , strict
                                                , fieldOverrides
                                                , field2Column);
            }
            return rv;
        }
        /*catch (IOException | URISyntaxException e) {
          throw new RuntimeException(e);
          } */ finally {
            DbUtils.closeQuietly((Connection) null, ps, rs);
        }
    }
}
