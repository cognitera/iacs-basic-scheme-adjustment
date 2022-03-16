package gr.cognitera.iacs.basic_scheme_adjustment;

import java.sql.SQLException;
import java.sql.ResultSet;


import org.apache.log4j.Logger;
import org.junit.Assert;


import gr.cognitera.util.jdbc.CustomFieldReader;

public class RightSourceFieldReader implements CustomFieldReader<RightSource> {
    final static Logger logger = Logger.getLogger(RightSourceFieldReader.class);

    @Override
    public Class<RightSource> getHandledClass() {
        return RightSource.class;
    }

    public RightSourceFieldReader(final String columnLabel) {
        this.columnLabel = columnLabel;
    }

    private final String columnLabel;

    @Override
    public RightSource read(final ResultSet rs) throws SQLException {
        final Integer code = rs.getInt(this.columnLabel);
        if (code==null)
            return null;
        else {
            final RightSource rv = RightSource.fromCode(code);
            Assert.assertNotNull(rv);
            return rv;
        }
    }
}