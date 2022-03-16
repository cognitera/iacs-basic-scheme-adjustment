package gr.cognitera.util.jdbc;

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
import java.math.BigInteger;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.apache.log4j.Logger;

import com.google.common.base.Throwables;
import com.google.gson.reflect.TypeToken;

import gr.cognitera.util.base.Util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class ResultSetHelper {

    final static Logger logger = Logger.getLogger(ResultSetHelper.class);    

    private ResultSetHelper() {
    }

    @SuppressFBWarnings(value="NP_BOOLEAN_RETURN_NULL",
                        justification="client side code handles null")
    public static Boolean getBoolean(ResultSet rs, int n) throws SQLException {
        Boolean retValue = rs.getBoolean(n);
        if (rs.wasNull())
            return null;
        else
            return retValue;
    }

    @SuppressFBWarnings(value="NP_BOOLEAN_RETURN_NULL",
                        justification="client side code handles null")    
    public static Boolean getBoolean(ResultSet rs, String columnLabel) throws SQLException {
        Boolean retValue = rs.getBoolean(columnLabel);
        if (rs.wasNull())
            return null;
        else
            return retValue;
    }

    public static Byte getByte(ResultSet rs, int n) throws SQLException {
        Byte retValue = rs.getByte(n);
        if (rs.wasNull())
            return null;
        else
            return retValue;
    }

    public static Byte getByte(ResultSet rs, String columnLabel) throws SQLException {
        Byte retValue = rs.getByte(columnLabel);
        if (rs.wasNull())
            return null;
        else
            return retValue;
    }

    public static Short getShort(ResultSet rs, int n) throws SQLException {
        Short retValue = rs.getShort(n);
        if (rs.wasNull())
            return null;
        else
            return retValue;
    }

    public static Short getShort(ResultSet rs, String columnLabel) throws SQLException {
        Short retValue = rs.getShort(columnLabel);
        if (rs.wasNull())
            return null;
        else
            return retValue;
    }

    public static Integer getInteger(ResultSet rs, int n) throws SQLException {
        Integer retValue = rs.getInt(n);
        if (rs.wasNull())
            return null;
        else
            return retValue;
    }

    public static Integer getInteger(ResultSet rs, String columnLabel) throws SQLException {
        Integer retValue = rs.getInt(columnLabel);
        if (rs.wasNull())
            return null;
        else
            return retValue;
    }
    
    public static Long getLong(ResultSet rs, int n) throws SQLException {
        Long retValue = rs.getLong(n);
        if (rs.wasNull())
            return null;
        else
            return retValue;
    }

    public static Long getLong(ResultSet rs, String columnLabel) throws SQLException {
        Long retValue = rs.getLong(columnLabel);
        if (rs.wasNull())
            return null;
        else
            return retValue;
    }

    public static Float getFloat(ResultSet rs, int n) throws SQLException {
        Float rv = rs.getFloat(n);
        if (rs.wasNull())
            return null;
        else
            return rv;
    }

    public static Float getFloat(ResultSet rs, String columnLabel) throws SQLException {
        Float rv = rs.getFloat(columnLabel);
        if (rs.wasNull())
            return null;
        else
            return rv;
    }

    public static Double getDouble(ResultSet rs, int n) throws SQLException {
        Double rv = rs.getDouble(n);
        if (rs.wasNull())
            return null;
        else
            return rv;
    }

    public static Double getDouble(ResultSet rs, String columnLabel) throws SQLException {
        Double rv = rs.getDouble(columnLabel);
        if (rs.wasNull())
            return null;
        else
            return rv;
    }

    public static BigDecimal getBigDecimal(ResultSet rs, String columnLabel) throws SQLException {
        return rs.getBigDecimal(columnLabel);
    }    


    public static String getJDBCTypeName(int jdbcType) {
        Map<Integer, String> map = new HashMap<>();

        // Get all field in java.sql.Types
        Field[] fields = Types.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                String name = fields[i].getName();
                Integer value = (Integer) fields[i].get(null);
                map.put(value, name);
            } catch (IllegalAccessException e) {
                // suppress
            }
        }
        return map.get(jdbcType);
    }


    /**
     * Translate a {@code java.sql.Timestamp} value to milliseconds since the Unix Epoch (MSSE)  
     *
     * @param rs the {@code ResultSet} object
     * @param columnLabel the name of the column on which the {@code java.sql.Timestamp} value is found
     * @return corresponding number of milliseconds since the Epoch or null if the column was null
     * @throws SQLException if excrement happened
     *
     */
    public static Long getTimestampAsMSSE(ResultSet rs, String columnLabel) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnLabel);
        return timestamp==null?null:timestamp.getTime();        
    }

    /**
     * Translates a {@code java.sql.Timestamp} value to seconds since the Unix Epoch (SSE)  
     *
     * @param rs the {@code ResultSet} object
     * @param columnLabel the name of the column on which the {@code java.sql.Timestamp} value is found
     * @return corresponding number of seconds since the Epoch or null if the column was null
     * @throws SQLException if excrement happened
     *
     */    
    public static Integer getTimestampAsSSE(ResultSet rs, String columnLabel) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnLabel);
        return timestamp==null?null: Util.castLongToInt(timestamp.getTime()/1000);
    }    

    public static String getCLOBAsString(ResultSet rs, String columnLabel) throws SQLException {
        Clob clob = rs.getClob(columnLabel);
        if (clob==null)
            return null;
        else {
            long lengthL = clob.length();
            if (Util.canBeSafelyCastToInt(lengthL)) {
                int length = Util.castLongToInt(lengthL);
                return clob.getSubString(1, length); // the first character is at position 1 (really!)
            } else {
                throw new ResultSetHelperException(String.format("Column [%s] has a length of [%d] and so cannot be reasonably cast to a String"
                                                                 , columnLabel
                                                                 , lengthL));
            }
        }
    }

    public static Character getStringAsCharacter(ResultSet rs, String columnLabel) throws SQLException {
        String v = rs.getString(columnLabel);
        if (v==null)
            return null;
        else {
            if (v.length()!=1)
                throw new ResultSetHelperException(String.format("Column [%s] presumably holds a CHAR, yet it was non-null and with a length of [%d]"
                                                                 , columnLabel
                                                                 , v.length()));
            else
                return v.charAt(v.length()-1);
        }
    }

    public static Boolean getYNStringAsBoolean(final ResultSet rs, final String columnLabel) throws SQLException {
        return getStringAsBoolean(rs, columnLabel, "Y", "N");
    }

    // this is not really different than calling rs.getBytes(columnLabel) directly. I am letting it live as an example
    // of this method since the Blob object offers a more advanced API (which is however not taken advantage of in the
    // below method)
    public static byte[] getBytes(final ResultSet rs, final String columnLabel) throws SQLException {
        Blob blob = null;
        try {
            blob           = rs.getBlob(columnLabel);
            int blobLength = (int) blob.length();
            byte[] bytes   = blob.getBytes(1, blobLength);
            return bytes;
        } finally {
            //release the blob and free up memory. (since JDBC 4.0)
            if (blob!=null)
                blob.free();
        }
    }

    @SuppressFBWarnings(value="NP_BOOLEAN_RETURN_NULL",
                        justification="client side code handles null")
    public static Boolean getStringAsBoolean(final ResultSet rs, final String columnLabel, final String valueForTrue, final String valueForFalse) throws SQLException {
        final String v = rs.getString(columnLabel);
        if (v==null)
            return null;
        else {
            if (v.equals(valueForTrue))
                return true;
            else if (v.equals(valueForFalse))
                return false;
            else
                throw new ResultSetHelperException(String.format("Column [%s] could not be mapped to Boolean; had a value of [%s] whereas I only know how to handle [%s] (for true) and [%s] (for false)"
                                                                 , columnLabel
                                                                 , v
                                                                 , valueForTrue
                                                                 , valueForFalse));
        }
    }


    public static Map<String, CustomFieldReader<?>> prepareFieldOverrides(Logger logger, Object ... columnLabelsAndCustomFieldReaders) {
        Map<String, CustomFieldReader<?>> rv = new LinkedHashMap<>();
        boolean customFieldReaderMayAppear = false;
        List<String> columnLabelsForThisCustomFieldReader = new ArrayList<>();
        for (int i = 0 ; i < columnLabelsAndCustomFieldReaders.length ; i++) {
            Object o = columnLabelsAndCustomFieldReaders[i];
            if (o.getClass()==String.class) {
                columnLabelsForThisCustomFieldReader.add((String) o);
                customFieldReaderMayAppear = true;
            } else if (o instanceof CustomFieldReader) {
                Assert.assertTrue(String.format("On index [%d] I was not expecting a [%s] yet; yet an object of class [%s] was encountered"
                                                , i
                                                , CustomFieldReader.class.getName()
                                                , o.getClass().getName())
                                  , customFieldReaderMayAppear);
                Assert.assertTrue(String.format("On index [%d] I was expecting at least 1 columnLabel to have been accumulated, yet none were present"
                                                 , i)
                                  , !columnLabelsForThisCustomFieldReader.isEmpty());
                CustomFieldReader customFieldReader = (CustomFieldReader) o;
                for (String columnLabelForThisCustomFieldReader: columnLabelsForThisCustomFieldReader) {
                    Assert.assertNull(String.format("Column label [%s] already encountered"
                                                    , columnLabelForThisCustomFieldReader)
                                      , rv.put(columnLabelForThisCustomFieldReader, customFieldReader));
                }
                columnLabelsForThisCustomFieldReader.clear();
                customFieldReaderMayAppear = false;
            } else
                Assert.fail(String.format("On index [%d] an object of class [%s] (which I've no use of) was encountered"
                                          , i
                                          , o.getClass().getName()));
        }
        return rv;
    }

    private static void  sanityTestForFieldOverrides(Class<?> klass, Map<String, CustomFieldReader<?>> fieldOverrides) throws NoSuchFieldException {
        for (Map.Entry<String, CustomFieldReader<?>> entry: fieldOverrides.entrySet()) {
            final String fieldOverrideColumnLabel        = entry.getKey();
            final CustomFieldReader<?> customFieldReader = entry.getValue();
            Field field = klass.getField(fieldOverrideColumnLabel);
            Class<?> fieldClass = field.getType();
            Assert.assertTrue(String.format("The class of field [%s] in class [%s] was [%s] whereas the customFieldReader of class [%s] can only handle class [%s]"
                                            , field.getName()
                                            , klass.getName()
                                            , fieldClass.getName()
                                            , customFieldReader.getClass().getName()
                                            , customFieldReader.getHandledClass().getName())
                              ,fieldClass == customFieldReader.getHandledClass());
            // don't worry about assignability at this point, strict equality will do for the time being
        }
    }

    public static <T> T readObject(final Logger _logger
                                   , final ResultSet rs
                                   , final Class<T> klass
                                   , final boolean strict
                                   , final IFieldOverridesProvider fieldOverridesProvider
                                   , final Map<String, String> field2Column) throws SQLException {
        return readObject(_logger
                          , rs
                          , klass
                          , strict
                          , fieldOverridesProvider.provideFieldOverridesBasedOnAssumedTypology(klass)
                          , field2Column
                          );
    }

    public static Map<String, String> prepareIgnoreColumns(final String ...columns) {
        final Map<String, String> rv = new LinkedHashMap<>();
        for (final String column: columns)
            rv.put(column, null);
        return rv;
    }

    /*
     *
     * if [strict] then unhandled property classes are not tolerated 
     *
     */
    public static <T> T readObject(final Logger _logger
                                   , final ResultSet rs
                                   , final Class<T> klass
                                   , final boolean strict
                                   , Map<String, CustomFieldReader<?>> fieldOverrides
                                   , Map<String, String> field2Column) throws SQLException {
        try {
            Logger logger = _logger;
            if (logger == null)
                logger = ResultSetHelper.logger;
            Assert.assertNotNull(logger);
            if (fieldOverrides==null)
                fieldOverrides = new LinkedHashMap<>();
            sanityTestForFieldOverrides(klass, fieldOverrides);
            final Constructor<T> constructor = klass.getConstructor();
            final T t = constructor.newInstance();
            final Field[] fields = klass.getFields();
            for (Field field: fields) {
                if (fieldOverrides.containsKey(field.getName())) {
                    final CustomFieldReader<?> customFieldReader = fieldOverrides.get(field.getName());
                    final Object o = customFieldReader.read(rs);
                    field.set(t, o);
                    logger.trace(String.format("field overrides: field [%s] in class [%s] is overriden and so was read by the custom field reader [%s] which assigned it a value of [%s]"
                                               , field.getName()
                                               , klass.getName()
                                               , customFieldReader.getClass()
                                               , o));
                } else {
                    Class<?> fieldClass = field.getType();
                    logger.trace(String.format("Field [%s] in class [%s] is of class [%s]"
                                               , field.getName()
                                               , klass.getName()
                                               , fieldClass.getName()));
                    String columnName = field.getName();
                    if ((field2Column!=null) && (field2Column.containsKey(field.getName()))) {
                        columnName = field2Column.get(field.getName());
                        logger.trace(String.format("field [%s] maps to column [%s]\n"
                                                   , field.getName()
                                                   , columnName));
                    }

                    if (columnName == null)
                        continue;
                    
                    if (fieldClass == String.class) {
                        final String value = rs.getString(columnName);
                        logger.trace(String.format("String field [%s] has value [%s]%n"
                                                   , columnName
                                                   , value));
                        field.set(t, value);
                    } else if (fieldClass == Character.class) {
                        final Character value = getStringAsCharacter(rs, columnName);
                        logger.trace(String.format("Character field [%s] has value [%s]%n"
                                                   , columnName
                                                   , value));
                        field.set(t, value);
                    } else if (fieldClass == Integer.class) {
                        final Integer value = ResultSetHelper.getInteger(rs, columnName);
                        logger.trace(String.format("Integer field [%s] has value [%s]%n"
                                                   , columnName
                                                   , value));
                        field.set(t, value);                        
                    } else if (fieldClass == int.class) {
                        final int i = ResultSetHelper.getInteger(rs, columnName);
                        logger.trace(String.format("int field [%s] has value [%d]%n"
                                                   , columnName
                                                   , i));
                        field.setInt(t, i);
                    } else if (fieldClass == long.class) {
                        try {
                            final long i = ResultSetHelper.getLong(rs, columnName);
                            logger.trace(String.format("int field [%s] has value [%d]%n"
                                                       , columnName
                                                       , i));
                            field.setLong(t, i);
                        } catch (SQLException e) {
                            // 
                            final String ORACLE_FAIL_MSG = "Invalid column type: getLong not implemented for class oracle.jdbc.driver.T4CRowidAccessor";
                            if (e.getMessage().equals(ORACLE_FAIL_MSG)) {
                                try {
                                    final BigDecimal decimal = rs.getBigDecimal(columnName);
                                    final BigInteger mob = (decimal == null ? null : decimal.toBigInteger());
                                    field.setLong(t, mob.longValue());
                                } catch (SQLException e2) {
                                    final String ORACLE_FAIL_MSG2 = "Invalid column type: getBigDecimal not implemented for class oracle.jdbc.driver.T4CRowidAccessor";
                                    if (e2.getMessage().equals(ORACLE_FAIL_MSG2)) {
                                        final String s = rs.getString(columnName);
                                        field.setLong(t, Long.parseLong(s));
                                    } else {
                                        throw e2; // TODO
                                    }
                                }
                            } else {
                                logger.error("the message encountered was not expected so I couldn't try the workaround for Oracle drivers");
                                throw e;
                            }
                        }
                    } else if (fieldClass == Float.class) {
                        final Float value = ResultSetHelper.getFloat(rs, columnName);
                        logger.trace(String.format("Float field [%s] has value [%s]%n"
                                                   , columnName
                                                   , value));
                        field.set(t, value);                        
                    } else if (fieldClass == Double.class) {
                        final Double value = ResultSetHelper.getDouble(rs, columnName);
                        logger.trace(String.format("Double field [%s] has value [%s]%n"
                                                   , columnName
                                                   , value));
                        field.set(t, value);
                    } else if (fieldClass == double.class) {
                        final double value = ResultSetHelper.getDouble(rs, columnName);
                        logger.trace(String.format("double field [%s] has value [%s]%n"
                                                   , columnName
                                                   , value));
                        field.setDouble(t, value);
                    } else if (fieldClass == Boolean.class) {
                        final Boolean value = ResultSetHelper.getBoolean(rs, columnName);
                        logger.trace(String.format("Boolean field [%s] has value [%s]%n"
                                                   , columnName
                                                   , value));
                        field.set(t, value);
                    } else if (fieldClass == boolean.class) {
                        final boolean b = ResultSetHelper.getBoolean(rs, columnName);
                        logger.trace(String.format("int field [%s] has value [%b]%n"
                                                   , columnName
                                                   , b));
                        field.setBoolean(t, b);
                    } else if (fieldClass == char.class) {
                        final Character c = ResultSetHelper.getStringAsCharacter(rs, columnName);
                        Assert.assertNotNull(c);
                        field.setChar(t, c);
                    } else if (fieldClass == Timestamp.class) {
                        final Timestamp ts = rs.getTimestamp(columnName);
                        field.set(t, ts);
                    } else if (fieldClass == byte[].class) {
                        final byte[] bytes = ResultSetHelper.getBytes(rs, columnName);
                        field.set(t, bytes);
                    } else if (fieldClass == BigDecimal.class) {
                        final BigDecimal bd = ResultSetHelper.getBigDecimal(rs, columnName);
                        field.set(t, bd);
                    } else {
                        if (strict)
                            Assert.fail(String.format("Unhandled class: %s", fieldClass.getName()));
                    }
                } // if (fieldOverride.containsKey(field)) :: else
            } // for
            return t;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            logger.error(Throwables.getStackTraceAsString(e));
            throw new RuntimeException(e);
        }
    }
}
