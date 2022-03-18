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

    @Override
    public RightSource read(final ResultSet rs, final String columnLabel) throws SQLException {
        final Integer code = rs.getInt(columnLabel);
        if (code==null)
            return null;
        else {
            final RightSource rv = RightSource.fromCode(code);
            Assert.assertNotNull(rv);
            return rv;
        }
    }
}
