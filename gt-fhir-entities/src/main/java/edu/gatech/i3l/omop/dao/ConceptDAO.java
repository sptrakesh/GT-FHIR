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

        final String sql = "select c from Concept c where c.vocabulary.id = :vocabulary and c.conceptCode = :conceptCode";
        TypedQuery<? extends BaseResourceEntity> query = DAO.getInstance().getEntityManager().createQuery(sql, Concept.class);
        query.setParameter("vocabulary", vocabulary);
        query.setParameter("conceptCode", conceptCode);
        List<? extends BaseResourceEntity> results = query.getResultList();
        if (results.isEmpty()) return null;

        final Concept c = (Concept) results.get(0);
        cache.put(key,c.getId());
        return c.getId();
    }

    private String hash(final String conceptCode, final String vocabulary) {
        return format("%s:%s", vocabulary, conceptCode);
    }

    private static final Map<String,Long> cache = new ConcurrentHashMap<>();
    private static final ConceptDAO singleton = new ConceptDAO();
}
