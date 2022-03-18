package gr.cognitera.iacs.basic_scheme_adjustment;

import java.sql.SQLException;
import java.sql.ResultSet;


import org.apache.log4j.Logger;
import org.junit.Assert;


import gr.cognitera.util.jdbc.CustomFieldReader;

public class RightTypeFieldReader implements CustomFieldReader<RightType> {
    final static Logger logger = Logger.getLogger(RightTypeFieldReader.class);

    @Override
    public boolean equals(final Object o) {
        return o.getClass().equals(this.getClass());
    }

    @Override
    public Class<RightType> getHandledClass() {
        return RightType.class;
    }
    @Override
    public RightType read(final ResultSet rs, final String columnLabel) throws SQLException {
        final Integer code = rs.getInt(columnLabel);
        if (code==null)
            return null;
        else {
            final RightType rv = RightType.fromCode(code);
            Assert.assertNotNull(rv);
            return rv;
        }
    }
}
