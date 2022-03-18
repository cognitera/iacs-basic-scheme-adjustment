package gr.cognitera.util.jdbc;

import java.sql.SQLException;
import java.sql.ResultSet;


import java.time.Instant;



import gr.cognitera.util.time.ISO8601Util;



public class ISO8601NoTZImplicitUTCFieldReader implements CustomFieldReader<Instant> {


    @Override
    public Class<Instant> getHandledClass() {
        return Instant.class;
    }

    @Override
    public Instant read(final ResultSet rs, final String columnLabel) throws SQLException {
        String rv = rs.getString(columnLabel);
        if (rv==null)
            return null;
        else
            return ISO8601Util.parseISO8601NoTZImplicitUTC(rv);
    }
}
