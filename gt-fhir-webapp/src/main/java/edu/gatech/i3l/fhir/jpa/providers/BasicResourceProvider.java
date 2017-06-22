package edu.gatech.i3l.fhir.jpa.providers;


import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.dstu2.resource.Basic;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.StringParam;
import edu.gatech.i3l.fhir.jpa.dao.SearchParameterMap;

/**
 * Created by rakesh.vidyadharan on 6/20/17.
 */
public class BasicResourceProvider extends JpaResourceProviderDstu2<Basic> {
    @Override
    public Class<? extends IResource> getResourceType() {
        return Basic.class;
    }

    @Search()
    public ca.uhn.fhir.rest.server.IBundleProvider search(
            javax.servlet.http.HttpServletRequest theServletRequest,

            @Description(shortDefinition = "The resource identity")
            @OptionalParam(name = "_id")
                    StringParam id,

            @Description(shortDefinition = "Patient associated with entity")
            @OptionalParam(name = "patient", targetTypes = {Patient.class})
                    ReferenceAndListParam patient,

            @Description(shortDefinition = "Patient associated with entity")
            @OptionalParam(name = "subject", targetTypes = {Patient.class})
                    ReferenceAndListParam subject,

            @Sort
                    SortSpec theSort,

            @Count
                    Integer theCount
    ) {
        startRequest(theServletRequest);
        try {
            SearchParameterMap paramMap = new SearchParameterMap();
            paramMap.add("_id", id);
            paramMap.add("patient", patient);
            paramMap.add("subject", subject);
            paramMap.setSort(theSort);
            paramMap.setCount(theCount);

            ca.uhn.fhir.rest.server.IBundleProvider retVal = getDao().search(paramMap);
            return retVal;
        } finally {
            endRequest(theServletRequest);
        }
    }
}
