package gr.cognitera.util.jdbc;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.Clob;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.apache.log4j.Logger;

import com.google.common.base.Throwables;

import gr.cognitera.util.base.Util;

public final class CallableStatementHelper {

    final static Logger logger = Logger.getLogger(CallableStatementHelper.class);

    private CallableStatementHelper() {
    }

    public static Integer getInteger(CallableStatement rs, int n) throws SQLException {
        Integer retValue = rs.getInt(n);
        if (rs.wasNull())
            return null;
        else
            return retValue;
    }

    public static Integer getInteger(CallableStatement rs, String columnLabel) throws SQLException {
        Integer retValue = rs.getInt(columnLabel);
        if (rs.wasNull())
            return null;
        else
            return retValue;
    }

    public static void setInteger(CallableStatement cs, int idx, Integer value) throws SQLException {
        if (value!=null)
            cs.setInt(idx, value);
        else
            cs.setNull(idx, Types.INTEGER);
    }
    
}
