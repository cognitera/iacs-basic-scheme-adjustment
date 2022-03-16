package gr.cognitera.iacs.basic_scheme_adjustment;

import java.sql.SQLException;
import java.sql.ResultSet;


import org.apache.log4j.Logger;
import org.junit.Assert;


import gr.cognitera.util.jdbc.CustomFieldReader;

public class RightTypeFieldReader implements CustomFieldReader<RightType> {
    final static Logger logger = Logger.getLogger(RightTypeFieldReader.class);

    @Override
    public Class<RightType> getHandledClass() {
        return RightType.class;
    }

    public RightTypeFieldReader(final String columnLabel) {
        this.columnLabel = columnLabel;
    }

    private final String columnLabel;

    @Override
    public RightType read(final ResultSet rs) throws SQLException {
        final Integer code = rs.getInt(this.columnLabel);
        if (code==null)
            return null;
        else {
            final RightType rv = RightType.fromCode(code);
            Assert.assertNotNull(rv);
            return rv;
        }
    }
}
