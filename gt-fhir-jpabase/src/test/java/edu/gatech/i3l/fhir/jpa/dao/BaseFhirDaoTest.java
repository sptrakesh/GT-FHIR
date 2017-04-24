package edu.gatech.i3l.fhir.jpa.dao;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import edu.gatech.i3l.fhir.jpa.util.DaoUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BaseFhirDaoTest  extends BaseJpaTest {

	private static FhirContext ourCtx = FhirContext.forDstu2();
	
	@Test
	public void testTranslateMatchUrl() {
		SearchParameterMap match = DaoUtils.translateMatchUrl("Condition?subject=304&_lastUpdated=>2011-01-01T11:12:21.0000Z", ourCtx, ourCtx.getResourceDefinition(Condition.class));
		assertEquals("2011-01-01T11:12:21.0000Z", match.getLastUpdated().getLowerBound().getValueAsString());
	}
	
}
