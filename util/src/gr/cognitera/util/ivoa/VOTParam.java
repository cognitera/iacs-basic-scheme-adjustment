package gr.cognitera.util.ivoa;

import com.google.common.base.MoreObjects.ToStringHelper;


public final class VOTParam extends VOTField {

    public final String value;

    public VOTParam(final String id,
                    final String datatype,
                    final String name,
                    final String precision,
                    final String ucd,
                    final String unit,
                    final String width,
                    final String description,
                    final String value) {
        super(id, datatype, name, precision, ucd, unit, width, description);
        this.value = value;
    }
                    

    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
            .add("value", value)
            ;
    }
    @Override
    public String toString() {
        return toStringHelper().toString();
    }    

}
