package gr.cognitera.util.rest;

import java.net.URI;

public final class HttpStatusNotOKish extends Exception {

    private final URI    uri;
    private final String method;
    private final int    status;
    private final String response;

    public HttpStatusNotOKish(final URI uri, final String method, final int status, final String response) {
        super(String.format("At URI [%s], with method [%s]: status was [%d] and response was: [%s]"
                            , uri
                            , method
                            , status
                            , response));
        this.uri      = uri;
        this.method   = method;
        this.status   = status;
        this.response = response;
    }

    public URI getURI() {
        return uri;
    }

    public String getMethod() {
        return method;
    }

    public int getStatus() {
        return status;
    }

    public String getResponse() {
        return response;
    }
}
