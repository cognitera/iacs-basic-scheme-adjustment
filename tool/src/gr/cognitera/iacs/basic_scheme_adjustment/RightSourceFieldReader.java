package gr.cognitera.iacs.basic_scheme_adjustment;

import java.sql.SQLException;
import java.sql.ResultSet;


import org.junit.Assert;


import gr.cognitera.util.jdbc.CustomFieldReader;

public class RightSourceFieldReader implements CustomFieldReader<RightSource> {

    @Override
    public boolean equals(final Object o) {
        return o.getClass().equals(this.getClass());
    }

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
