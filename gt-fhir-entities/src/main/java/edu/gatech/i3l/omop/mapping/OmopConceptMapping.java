package edu.gatech.i3l.omop.mapping;

import edu.gatech.i3l.fhir.dstu2.entities.Concept;
import edu.gatech.i3l.fhir.dstu2.entities.Domain;
import edu.gatech.i3l.fhir.jpa.dao.BaseFhirDao;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class serves as cache for Concept values in Omop schema based database.
 * It is suggested to start a new thread and pass this singleton to run.
 *
 * @author Ismael Sarmento
 */
public class OmopConceptMapping implements Runnable {

    /*
     * Concepts' Classes
     */
    public static final String UNDEFINED = "Undefined";
    public static final String GENDER = "Gender";
    //	public static final String MARITAL_STATUS = "Marital Status";
    public static final String DRUG_EXPOSURE_TYPE = "Drug Type";
    public static final String CLINICAL_FINDING = "Clinical finding";
    public static final String QUALIFIER_VALUE = "Qualifier Value";
    public static final String PROCEDURE = "Procedure";
    public static final String OBSERVABLE_ENTITY = "Observable entity";
    public static final String CONDITION_TYPE = "Condition Type";
    public static final String LOINC_CODE = "LOINC Code";
    public static final String SPECIALTY = "Specialty";
    public static final String UCUM_CODE = "UCUM";
    public static final String UCUM_CODE_STANDARD = "UCUM Standard";
    public static final String UCUM_CODE_CUSTOM = "UCUM Custom";
    public static final String VISIT = "Visit";
    public static final String PLACE_OF_SERVICE = "Place of Service";
    private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(OmopConceptMapping.class);
    /*
     * Vocabularies
     */
//	private static final String OMOP_4_5 = "OMOP Vocabulary v4.5 20-Oct-2014";
//	private static final String GENDER_VOCABULARY = "HL7 Administrative Sex";
    private static final String SNOMED_CT = "SNOMED";
    private static final String LOINC = "LOINC";
    private static final String OMOP_CONDITION_TYPE = "OMOP Condition Occurrence Type";
    private static final String UCUM = "UCUM";
    private static OmopConceptMapping omopConceptMapping = new OmopConceptMapping();
    /**
     * A mapping for some of the existing concepts in the database. The key for the outter mapping is the Concept Class.
     * The inner map has the value(name) of the Concept as key and the respective id in the database as values in the map.
     */
    protected Map<String, Map<String, Long>> concepts = new HashMap<String, Map<String, Long>>();
//	private static final String CMS_PLACE_OF_SERVICE = "CMS Place of Service";
    private EntityManager entityManager;

    private OmopConceptMapping() {
    }

    public static OmopConceptMapping getInstance() {
        return omopConceptMapping;
    }

    public void loadConcepts() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        concepts.put(GENDER, findConceptMap(builder, GENDER, null));
        concepts.put(DRUG_EXPOSURE_TYPE, findConceptMap(builder, DRUG_EXPOSURE_TYPE, null));
        concepts.put(CLINICAL_FINDING, findConceptMap(builder, CLINICAL_FINDING, SNOMED_CT)); //TODO test size of the object/map
        concepts.put(CONDITION_TYPE, findConceptMap(builder, CONDITION_TYPE, OMOP_CONDITION_TYPE));
        concepts.put(QUALIFIER_VALUE, findConceptMap(builder, QUALIFIER_VALUE, null));
        concepts.put(LOINC_CODE, findConceptMap(builder, LOINC_CODE, LOINC));
        concepts.put(UCUM_CODE, findConceptMap(builder, UCUM_CODE, UCUM));
        concepts.put(UCUM_CODE_STANDARD, findConceptMap(builder, UCUM_CODE, UCUM));
        concepts.put(UCUM_CODE_CUSTOM, findConceptMap(builder, UCUM_CODE, UCUM));
        concepts.put(VISIT, findConceptMap(builder, VISIT, null));
    }

    /**
     * Searches on the database for the concepts, using, as filters, the concept class and the respective vocabulary name.
     *
     * @param builder
     * @param conceptClass
     * @param vocabularyName
     * @return A map containing the names(values) of the concepts and their respective id's in the database.
     */
    private Map<String, Long> findConceptMap(CriteriaBuilder builder, String conceptClass, String vocabularyName) {
        CriteriaQuery<Object[]> criteria = builder.createQuery(Object[].class);
        Root<Concept> from = criteria.from(Concept.class);
        Path<Long> idPath = from.get("id");
        Path<String> codePath = from.get("conceptCode");
        criteria.multiselect(codePath, idPath); //TODO unit test, order matters here
        Predicate p1 = builder.like(from.get("conceptClassId").as(String.class), conceptClass);
        if (vocabularyName != null) {
            Predicate p2 = builder.like(from.get("vocabulary").get("name").as(String.class), vocabularyName);
            criteria.where(builder.and(p1, p2));
        } else {
            criteria.where(builder.and(p1));
        }
        TypedQuery<Object[]> query = entityManager.createQuery(criteria);
        Map<String, Long> retVal = new HashMap<String, Long>();
        List<Object[]> resultList = query.getResultList();
        for (Object[] result : resultList) {
            retVal.put(((String) result[0]).toLowerCase(), (Long) result[1]);
        }
        return retVal;
    }

    @Override
    public void run() {
        String baseFhirDao = "myBaseDao";
        WebApplicationContext myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
        entityManager = myAppCtx.getBean(baseFhirDao, BaseFhirDao.class).getEntityManager();
        loadConcepts();
    }

    public Long get(String conceptValue, String... conceptClass) {
        Long retVal = null;
        for (int i = 0; i < conceptClass.length; i++) {
            Map<String, Long> concepts = this.concepts.get(conceptClass[i]);
            if (concepts != null) {
                retVal = concepts.get(conceptValue.toLowerCase());
                if (retVal != null) {
                    return retVal;
                }
            }
        }
        ourLog.warn("A respective value for {} '{}' could not be found in the database.", conceptClass, conceptValue);
        return retVal;
    }

    public Long get(String conceptCode) {
        TypedQuery<Long> query = entityManager.createNamedQuery("findConceptByCode", Long.class).setParameter("code", conceptCode);
        List<Long> results = query.getResultList();
        if (results.size() > 0)
            return results.get(0);
        else
            return null;
//		return (Long) query.getSingleResult();
    }

    public Map<String, Long> getConceptsForClass(String conceptClass) {
        return concepts.get(conceptClass);
    }

    public String getDomain(String conceptCode) {
        if (StringUtils.isBlank(conceptCode)) return null;
        final TypedQuery<Domain> query = entityManager.createNamedQuery("findDomainByCode", Domain.class).setParameter("code", conceptCode);
        final List<Domain> results = query.getResultList();
        return (!results.isEmpty()) ? results.get(0).getDomainId() : null;
    }

    public String getVocabularyReference(String vocabularyName) {
        TypedQuery<String> query = entityManager.createNamedQuery("findReferenceById", String.class).setParameter("value", vocabularyName);
        List<String> results = query.getResultList();
        if (results.size() > 0)
            return results.get(0);
        else
            return null;
//		return (String) query.getSingleResult();
    }

    public Object loadEntityById(Class<? extends BaseResourceEntity> class1, Long primaryKey) {
        return entityManager.find(class1, primaryKey);
    }

    // OK... we need to sort things out. I am piggybacking on this. I think we will need to refactor
    // the name this class to cover more generic needs of db query.
    public Object loadEntityBySource(Class<? extends BaseResourceEntity> class_, String tableName, String columnName, String value) {
        String query = "SELECT t" + " FROM " + tableName + " t WHERE " + columnName + " like :value";
        List<? extends BaseResourceEntity> results = entityManager.createQuery(query, class_)
                .setParameter("value", value).getResultList();
        if (results.size() > 0)
            return results.get(0);
        else
            return null;
    }

    public Object loadEntityByLocation(Class<? extends BaseResourceEntity> class_, String line1, String line2, String city, String state, String zipCode) {
        String query;
        Object result;
        List<? extends BaseResourceEntity> results;

        if (line2 != null) {
            query = "SELECT t FROM Location t WHERE address1 = :line1 AND address2 = :line2 AND city = :city AND state = :state AND zipCode = :zip";
            results = entityManager.createQuery(query, class_)
                    .setParameter("line1", line1)
                    .setParameter("line2", line2)
                    .setParameter("city", city)
                    .setParameter("state", state)
                    .setParameter("zip", zipCode)
                    .getResultList();
        } else {
            query = "SELECT t FROM Location t WHERE address1 = :line1 AND city = :city AND state = :state AND zipCode = :zip";
            results = entityManager.createQuery(query, class_)
                    .setParameter("line1", line1)
                    .setParameter("city", city)
                    .setParameter("state", state)
                    .setParameter("zip", zipCode)
                    .getResultList();
        }

        System.out.println("loadEntityByLocation:" + query + " result size:" + results.size());
        if (results.size() > 0) return results.get(0);
        else return null;
    }
}
