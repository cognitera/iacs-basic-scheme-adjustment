package gr.cognitera.util.xml;

public final class ResourceResolutionCoordinates {

    public final String type;
    public final String namespaceURI;
    public final String publicId;
    public final String systemId;
    public final String baseURI;

    
    public ResourceResolutionCoordinates(final String type
                                         , final String namespaceURI
                                         , final String publicId
                                         , final String systemId
                                         , final String baseURI) {
        this.type = type;
        this.namespaceURI = namespaceURI;
        this.publicId = publicId;
        this.systemId = systemId;
        this.baseURI = baseURI;
    }

}
