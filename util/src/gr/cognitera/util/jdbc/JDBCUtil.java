package gr.cognitera.util.jdbc;

import java.util.List;
import java.util.Arrays;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;

import org.apache.commons.dbutils.DbUtils;

import com.google.common.base.Joiner;

enum LOCK_MODE {
    EXCLUSIVE("EXCLUSIVE"), SHARE("SHARE");

    private LOCK_MODE(final String value) {
        this.value = value;
    }

    public final String value;
}


public final class JDBCUtil {

    private JDBCUtil() {}
    
    public static String commaSeparatedQuestionMarks(final int numOfQuestionMarks) {
        final String[] argumentsArr = new String[numOfQuestionMarks];
        Arrays.fill(argumentsArr, "?");
        final List<String> argumentsList = Arrays.asList(argumentsArr);
        return Joiner.on(", ").join(argumentsList);
    }

    public static String commaSeparatedStringPlaceholders(final int numOfPercentages) {
        final String[] argumentsArr = new String[numOfPercentages];
        Arrays.fill(argumentsArr, "%s");
        final List<String> argumentsList = Arrays.asList(argumentsArr);
        return Joiner.on(", ").join(argumentsList);
    }

 }
