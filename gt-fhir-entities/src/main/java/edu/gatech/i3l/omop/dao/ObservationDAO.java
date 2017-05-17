package edu.gatech.i3l.omop.dao;

import edu.gatech.i3l.fhir.dstu2.entities.Observation;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by rakesh.vidyadharan on 5/15/17.
 */
public class ObservationDAO {
    public static ObservationDAO getInstance() { return singleton; }

    public List<Observation> getDiastolicObservation(final Observation observation )
    {
        final EntityManager entityManager = DAO.getInstance().getEntityManager();
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Observation> criteria = builder.createQuery(Observation.class);
        final Root<Observation> from = criteria.from(Observation.class);
        criteria.select(from).where(
                builder.equal(from.get("observationConcept").get("id"), Observation.DIASTOLIC_CONCEPT_ID),
                builder.equal(from.get("person").get("id"), observation.getPerson().getId()),
                builder.equal(from.get("date"), observation.getDate()),
                builder.equal(from.get("time"), observation.getTime())
        );
        final TypedQuery<Observation> query = entityManager.createQuery(criteria);
        return query.getResultList();
    }

    private static final ObservationDAO singleton = new ObservationDAO();
    private ObservationDAO() {}
}
