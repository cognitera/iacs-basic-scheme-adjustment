package gr.cognitera.util.chandra;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public class CrossmatchUserTableConfig {

    public CrossmatchUserTableType type;
    public List<String> columns;


    public CrossmatchUserTableConfig(final CrossmatchUserTableType type
                                     , final List<String> columns) {
        Assert.assertNotNull(type);
        Assert.assertNotNull(columns);
        for (String column: columns)
            Assert.assertNotNull(column);
        Assert.assertTrue(String.format("Columns were {%s}, of size %d which is not >= 2"
                                        , Joiner.on(", ").join(columns)
                                        , columns.size())
                          , columns.size()>=2);
        this.type = type;
        this.columns = columns;
    }


    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
            .add("type"        , type)
            .add("columns"     , Joiner.on(", ").join(columns))
            ;
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null) return false;
        
        if (!(o instanceof CrossmatchUserTableConfig))
            return false;
 
        final CrossmatchUserTableConfig other = (CrossmatchUserTableConfig) o;
        System.out.printf("types are equal? %b\n", Objects.equals(type, other.type));
        System.out.printf("columns are equal? %b\n", Objects.equals(columns, other.columns));
        final int N = columns.size();
        for (int i = 0; i < N; i++) {
            final String v1 = columns.get(i);
            final String v2 = other.columns.get(i);
            if (!v1.equals(v2))
                System.out.printf("[%s] is not equal to [%s]"
                                  , v1, v2);
        }
                                  
        return Objects.equals(type, other.type) &&
            Objects.equals(columns, other.columns) ;
    }

    @Override
    public int hashCode(){
        return Objects.hash(type, columns);
    }
    
}

