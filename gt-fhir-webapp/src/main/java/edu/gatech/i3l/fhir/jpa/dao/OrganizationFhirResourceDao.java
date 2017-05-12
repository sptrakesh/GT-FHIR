package edu.gatech.i3l.fhir.jpa.dao;

import ca.uhn.fhir.model.dstu2.resource.Organization;
import edu.gatech.i3l.fhir.dstu2.entities.CareSite;
import edu.gatech.i3l.fhir.jpa.entity.IResourceEntity;
import edu.gatech.i3l.fhir.jpa.query.AbstractPredicateBuilder;
import edu.gatech.i3l.fhir.jpa.query.PredicateBuilder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

@Transactional(propagation = Propagation.REQUIRED)
public class OrganizationFhirResourceDao extends BaseFhirResourceDao<Organization> {

	public OrganizationFhirResourceDao() {
		super();
		setResourceEntity(CareSite.class);
		setValidateBean(true);
	}
	
	@Override
	public PredicateBuilder getPredicateBuilder() {
		return new AbstractPredicateBuilder() {
			@Override
			public Predicate translatePredicateString(Class<? extends IResourceEntity> entity, String theParamName,
                    String likeExpression, From<? extends IResourceEntity, ? extends IResourceEntity> from,
                    CriteriaBuilder theBuilder) {
			    switch ( theParamName ) {
                    case Organization.SP_NAME:
                        return theBuilder.like(from.get("careSiteName").as(String.class), likeExpression);
                    case Organization.SP_ADDRESS:
                        final Path location = from.get("location");
                        final Predicate lc1 = theBuilder.like(location.get("address1").as(String.class), likeExpression);
                        final Predicate lc2 = theBuilder.like(location.get("address2").as(String.class), likeExpression);
                        final Predicate lc3 = theBuilder.like(location.get("city").as(String.class), likeExpression);
                        final Predicate lc4 = theBuilder.like(location.get("state").as(String.class), likeExpression);
                        final Predicate lc5 = theBuilder.like(location.get("zipCode").as(String.class), likeExpression);
						final Predicate lc6 = theBuilder.like(location.get("country").as(String.class), likeExpression);
                        return theBuilder.or(lc1, lc2, lc3, lc4, lc5, lc6);
			    	default:
						return super.translatePredicateString(entity, theParamName, likeExpression, from, theBuilder);
				}
			}
		};
	}
}
