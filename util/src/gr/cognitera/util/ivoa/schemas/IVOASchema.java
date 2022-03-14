package gr.cognitera.util.ivoa.schemas;

import java.io.InputStream;
import javax.xml.transform.stream.StreamSource;

public enum IVOASchema {

    VOTABLE_1_1("VOTable-1.1.xsd")
    , VOSI_AVAILABILITY_1_0("VOSIAvailability-v1.0.xsd")
    , VOSI_CAPABILITIES_1_0("VOSICapabilities-v1.0.xsd");

    private final String fname;

    private IVOASchema(final String fname) {
        this.fname = fname;
    }


    public InputStream asInputStream() {
        return this.getClass().getResourceAsStream(fname);
    }

    public StreamSource asStreamSource() {
        return new StreamSource(asInputStream());
    }

}
