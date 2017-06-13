package edu.gatech.i3l.omop.dao;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

/**
 * Created by rakesh.vidyadharan on 5/30/17.
 */
public class ConceptDAO {
    public static final String SNOMED = "SNOMED";
    public static final String SNOMED_URL = "http://snomed.info/sct";

    public static ConceptDAO getInstance() {return singleton;}

    public Long getConcept(final String conceptCode, final String vocabularyOrUrl) {
        final String vocabulary = getVocabularyName(vocabularyOrUrl);
        final String key = hash(conceptCode, vocabulary);
        if (cache.containsKey(key)) return cache.get(key);

        final String sql = "select c.id from Concept c where c.vocabulary.id = :vocabulary and c.conceptCode = :conceptCode";
        TypedQuery<Long> query = DAO.getInstance().getEntityManager().createQuery(sql, Long.class);
        query.setParameter("vocabulary", vocabulary);
        query.setParameter("conceptCode", conceptCode);
        List<Long> results = query.getResultList();
        if (results.isEmpty()) return null;

        cache.put(key,results.get(0));
        return results.get(0);
    }

    public String getVocabularyName(final String systemUrl) {
        if (systemUrl == null) return null;
        switch (systemUrl) {
            case SNOMED_URL : return SNOMED;
            case "http://hl7.org/fhir/sname/icd-9-cm": return "ICD9CM";
            case "http://hl7.org/fhir/sname/icd-9-cm/procedure": return "ICD9Proc";
            case "http://www.ama-assn.org/go/cpt": return "CPT4";
            case "http://purl.bioontology.org/ontology/HCPCS": return "HCPCS";
            case "http://loinc.org": return "LOINC";
            case "http://www.nlm.nih.gov/research/umls/rxnorm": return "RxNorm";
            case "http://unitsofmeasure.org": return "UCUM";
            case "http://hl7.org/fhir/sname/ndc": return "NDC";
            case "urn:oid:2.16.840.1.113883.3.26.1.1": return "NCI";
            default: return systemUrl;
        }
    }

    public String getSystemUri(final String vocabulary) {
        if (vocabulary == null) return null;
        switch (vocabulary) {
            case SNOMED: return SNOMED_URL;
            case "ICD9CM": return "http://hl7.org/fhir/sid/icd-9-cm";
            case "ICD9Proc": return "http://hl7.org/fhir/sid/icd-9-cm/procedure";
            case "CPT4": return "http://www.ama-assn.org/go/cpt";
            case "HCPCS": return "http://purl.bioontology.org/ontology/HCPCS";
            case "LOINC": return "http://loinc.org";
            case "RxNorm": return "http://www.nlm.nih.gov/research/umls/rxnorm";
            case "UCUM": return "http://unitsofmeasure.org";
            case "NDC": return "http://hl7.org/fhir/sid/ndc";
            case "NCI": return "urn:oid:2.16.840.1.113883.3.26.1.1";
            default: return vocabulary;
        }
    }

    public Long getDefaultRace() {
        return getConcept("415226007", SNOMED);
    }

    private String hash(final String conceptCode, final String vocabulary) {
        return format("%s:%s", vocabulary, conceptCode);
    }

    private static final Map<String,Long> cache = new ConcurrentHashMap<>();
    private static final ConceptDAO singleton = new ConceptDAO();
}
