package edu.gatech.i3l.fhir.jpa.dao;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.rest.param.DateRangeParam;
import edu.gatech.i3l.fhir.dstu2.entities.Provider;
import edu.gatech.i3l.fhir.jpa.entity.IResourceEntity;
import edu.gatech.i3l.fhir.jpa.query.AbstractPredicateBuilder;
import edu.gatech.i3l.fhir.jpa.query.PredicateBuilder;

public class PractitionerFhirResourceDao extends BaseFhirResourceDao<Practitioner> {

	public PractitionerFhirResourceDao() {
		super();
		setResourceEntity(Provider.class);
		setValidateBean(true);
	}

	@Override
	public PredicateBuilder getPredicateBuilder() {
		return new AbstractPredicateBuilder() {
			@Override
			public Predicate translatePredicateString(Class<? extends IResourceEntity> entity, String theParamName, String likeExpression,
					From<? extends IResourceEntity, ? extends IResourceEntity> from, CriteriaBuilder theBuilder) {
				switch (theParamName) {
				case Practitioner.SP_NAME:
					return theBuilder.like(from.get("providerName").as(String.class), likeExpression);
				default:
					return super.translatePredicateString(entity, theParamName, likeExpression, from, theBuilder);
				}
			}
		};
	}
	
}
