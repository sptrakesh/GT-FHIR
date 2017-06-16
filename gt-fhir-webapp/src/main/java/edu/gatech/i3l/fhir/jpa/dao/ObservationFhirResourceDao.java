package edu.gatech.i3l.fhir.jpa.dao;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import edu.gatech.i3l.fhir.jpa.entity.IResourceEntity;
import edu.gatech.i3l.fhir.jpa.query.AbstractPredicateBuilder;
import edu.gatech.i3l.fhir.jpa.query.PredicateBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import static java.lang.String.format;

@Transactional(propagation = Propagation.REQUIRED)
public class ObservationFhirResourceDao extends BaseFhirResourceDao<Observation> {

    public ObservationFhirResourceDao() {
        super();
        setResourceEntity(edu.gatech.i3l.fhir.dstu2.entities.Observation.class); //TODO set this automatically; this is error prone since we need to remember to set this on each dao class
        setValidateBean(true);
    }

    @Override
    public PredicateBuilder getPredicateBuilder() {
        return new AbstractPredicateBuilder() {

            private static final String LOINC = "loinc";
            private static final String SNOMED = "snomed";
            private static final String ICD_9 = "icd-9";
            private static final String ICD_9_CM = "icd-9-cm";
            private static final String ICD_9_PROC = "icd-9-proc";
            private static final String RXNORM = "rxnorm";
            private static final String UCUM = "ucum";
            private static final String ICD_10 = "icd-10";

            @Override
            public Predicate addCommonPredicate(CriteriaBuilder builder, From<? extends IResourceEntity, ? extends IResourceEntity> from) {
//				builder.asc(from.get("id"));
                return builder.notEqual(from.get("observationConcept").get("id"), edu.gatech.i3l.fhir.dstu2.entities.Observation.DIASTOLIC_CONCEPT_ID);
                //In Omop database, the dictionary is static; that means we can reference id's directly: the id for the vocabulary RxNorm is 8
            }

            @Override
            public Predicate translatePredicateTokenSystem(Class<? extends IResourceEntity> entity, String theParamName, String system, From<? extends IResourceEntity, ? extends IResourceEntity> from,
                                                           CriteriaBuilder theBuilder) {
                Predicate predicate = null;
                if (system == null) {
                    return null;
                }

                system = getVocabularyName(system);

                Path<Object> path = null;
                switch (theParamName) {
                    case Observation.SP_CODE:
                        path = from.get("observationConcept").get("vocabulary").get("id");
                        break;
                    default:
                        break;
                }
                if (StringUtils.isNotBlank(system)) {
                    predicate = theBuilder.like(path.as(String.class), system + "%");
                }//	else {
//					return theBuilder.isNull(path); //WARNING originally, if the system is empty, then it would be checked for null systems
//				}
                return predicate;
            }

            private String getVocabularyName(String system) {
                if (system.contains(SNOMED)) {
                    return "SNOMED";
                } else if (system.contains(LOINC)) {
                    return "LOINC";
                } else if (system.contains(ICD_10)) {
                    return "ICD10";
                } else if (system.contains(ICD_9)) {
                    return "ICD9CM";
                } else if (system.contains(ICD_9_CM)) {
                    return "ICD9CM";
                } else if (system.contains(ICD_9_PROC)) {
                    return "ICD9Proc";
                } else if (system.contains(RXNORM)) {
                    return "RxNorm";
                } else if (system.contains(UCUM)) {
                    return "UCUM";
                }
                return "";
            }

            @Override
            public Predicate translatePredicateTokenCode(Class<? extends IResourceEntity> entity, String theParamName, String code, From<? extends IResourceEntity, ? extends IResourceEntity> from,
                                                         CriteriaBuilder theBuilder) {
                Predicate predicate = null;
                Path<Object> path = null;
                switch (theParamName) {
                    case Observation.SP_CODE:
                        path = from.get("observationConcept").get("conceptCode");
                        break;
                    default:
                        break;
                }
                if (StringUtils.isNotBlank(code)) {
                    predicate = theBuilder.equal(path, code);
                } //else {
//					return theBuilder.isNull(path);
//				}
                return predicate;
            }

        };
    }

}
