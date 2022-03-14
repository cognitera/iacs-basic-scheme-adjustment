package gr.cognitera.util.orcid;

import java.io.IOException;
import gr.cognitera.util.rest.HttpStatusNotOKish;

public interface IORCIDPersonRetriever {

    ORCIDPerson getPerson(final String orcid) throws IOException, HttpStatusNotOKish, CannotConstructORCIDPersonFromJson;

}
