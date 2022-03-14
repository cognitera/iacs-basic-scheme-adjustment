package gr.cognitera.util.jdbc;

import java.sql.SQLException;
import java.sql.ResultSet;


import java.time.Instant;

import org.apache.log4j.Logger;


import gr.cognitera.util.time.ISO8601Util;



public class ISO8601NoTZImplicitUTCFieldReader implements CustomFieldReader<Instant> {
    final static Logger logger = Logger.getLogger(ISO8601NoTZImplicitUTCFieldReader.class);

    public ISO8601NoTZImplicitUTCFieldReader(final String columnLabel) {
        this.columnLabel = columnLabel;
    }

    private final String columnLabel;

    @Override
    public Class<Instant> getHandledClass() {
        return Instant.class;
    }

    @Override
    public Instant read(ResultSet rs) throws SQLException {
        String rv = rs.getString(this.columnLabel);
        if (rv==null)
            return null;
        else
            return ISO8601Util.parseISO8601NoTZImplicitUTC(rv);
    }
}
