package edu.gatech.i3l.omop.dao;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import edu.gatech.i3l.fhir.dstu2.entities.Concept;

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
    public final Config config = ConfigFactory.load();

    public static ConceptDAO getInstance() {return singleton;}

    public Long getConcept(final CodingDt concept) {
        return (concept != null) ? getConcept(concept.getCode(), concept.getSystem()) : null;
    }

    public Long getConcept(final String conceptCode, final String vocabularyOrUrl) {
        final String vocabulary = getVocabularyName(vocabularyOrUrl);
        final String key = hash(conceptCode, vocabulary);
        if (cache.containsKey(key)) return cache.get(key);

        final String sql = "select c.id from Concept c where c.vocabulary = :vocabulary and c.conceptCode = :conceptCode";
        TypedQuery<Long> query = DAO.getInstance().getEntityManager().createQuery(sql, Long.class);
        query.setParameter("vocabulary", vocabulary);
        query.setParameter("conceptCode", conceptCode);
        List<Long> results = query.getResultList();
        if (results.isEmpty()) return null;

        cache.put(key,results.get(0));
        return results.get(0);
    }

    public Concept getGenderConcept(final String gender) {
        Config conf = null;

        if (gender != null)
        {
            switch (gender) {
                case "male":
                    conf = config.getConfig("concept.gender.male");
                    break;
                case "female":
                    conf = config.getConfig("concept.gender.female");
                    break;
            }
        }

        if (conf == null) return null;
        final Long id = getConcept(conf.getString("code"), conf.getString("url"));
        return (id != null) ? new Concept(id) : null;
    }

    public String getVocabularyName(final String systemUrl) {
        if (systemUrl == null) return null;
        final String url = (systemUrl.indexOf(':') != -1) ? format("\"%s\"", systemUrl) : systemUrl;
        final Config conf = config.getConfig("concept.urlToVocabulary");
        return (conf.hasPath(url)) ? conf.getString(url) : systemUrl;
    }

    public String getSystemUri(final String vocabulary) {
        if (vocabulary == null) return null;
        final Config conf = config.getConfig("concept.vocabularyToUrl");
        return (conf.hasPath(vocabulary)) ? conf.getString(vocabulary) : vocabulary;
    }

    public Long getDefaultRace() {
        final Config conf = config.getConfig("concept.race.default");
        return getConcept(conf.getString("code"), conf.getString("url"));
    }

    public CodingDt getCodingDt(final String system, final String code) {
        return new CodingDt(system, code);
    }

    private String hash(final String conceptCode, final String vocabulary) {
        return format("%s:%s", vocabulary, conceptCode);
    }

    private static final Map<String,Long> cache = new ConcurrentHashMap<>();
    private static final ConceptDAO singleton = new ConceptDAO();
}
