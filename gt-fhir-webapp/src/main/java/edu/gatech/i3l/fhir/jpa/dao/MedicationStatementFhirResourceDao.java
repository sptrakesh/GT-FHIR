package edu.gatech.i3l.fhir.jpa.dao;

import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import edu.gatech.i3l.fhir.dstu2.entities.DrugExposureStatement;
import edu.gatech.i3l.fhir.jpa.query.AbstractPredicateBuilder;
import edu.gatech.i3l.fhir.jpa.query.PredicateBuilder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED)
public class MedicationStatementFhirResourceDao extends BaseFhirResourceDao<MedicationStatement> {

	public MedicationStatementFhirResourceDao() {
		super();
		setResourceEntity(DrugExposureStatement.class);
		setValidateBean(true);
	}

	@Override
	public PredicateBuilder getPredicateBuilder() {
		return new AbstractPredicateBuilder() {};
	}
}
