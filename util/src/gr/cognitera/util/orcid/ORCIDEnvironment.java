package gr.cognitera.util.orcid;


/**
 * Holds the host names for a number of ORCID-defined environments
 * (sandbox, public and member) 
 *
 */
public enum ORCIDEnvironment {

    SANDBOX_MEMBER_API             ("api.sandbox.orcid.org"),
    SANDBOX_PUBLIC_API             ("pub.sandbox.orcid.org"),
    PRODUCTION_REGISTRY_MEMBER_API ("api.orcid.org"),
    PRODUCTION_REGISTRY_PUBLIC_API ("pub.orcid.org");

    private final String host;

    private ORCIDEnvironment(final String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }
}
