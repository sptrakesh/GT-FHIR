package edu.gatech.i3l.omop.dao;

import edu.gatech.i3l.fhir.dstu2.entities.Concept;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

/**
 * Created by rakesh.vidyadharan on 5/30/17.
 */
public class ConceptDAO {
    public static ConceptDAO getInstance() {return singleton;}

    public Long getConcept(final String conceptCode, final String vocabulary) {
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

    public Long getDefaultRace() {
        return getConcept("415226007", "SNOMED");
    }

    private String hash(final String conceptCode, final String vocabulary) {
        return format("%s:%s", vocabulary, conceptCode);
    }

    private static final Map<String,Long> cache = new ConcurrentHashMap<>();
    private static final ConceptDAO singleton = new ConceptDAO();
}
