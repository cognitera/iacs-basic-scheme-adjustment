package gr.cognitera.util.jdbc;

import java.util.Set;
import java.util.Map;
import java.sql.ResultSet;
import java.sql.SQLException;


public interface CustomFieldReader<T> {


    public abstract Class<T> getHandledClass();
    public abstract T read(final ResultSet rs, final String columnName) throws SQLException;


    public static boolean equals(final Map<String, CustomFieldReader<?>> a
                                 , final Map<String, CustomFieldReader<?>> b) {
        final Set<String> aKeys = a.keySet();
        final Set<String> bKeys = a.keySet();
        if (!aKeys.equals(bKeys)) {
            System.out.printf("a\n");
            return false;
        }
        for (final Map.Entry<String, CustomFieldReader<?>> entry : a.entrySet()) {
            final CustomFieldReader<?> aReader = entry.getValue();
            final CustomFieldReader<?> bReader = b.get(entry.getKey());
            System.out.printf(entry.getKey());
            if (bReader==null) {
                System.out.printf("b\n");
                return false;
            } if (!aReader.equals(bReader)) {
                System.out.printf("c\n");
                return false;
            }
        }
        return true;
    }


}
