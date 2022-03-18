package gr.cognitera.util.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;


public interface CustomFieldReader<T> {


    public abstract Class<T> getHandledClass();
    public abstract T read(final ResultSet rs, final String columnName) throws SQLException;


}
