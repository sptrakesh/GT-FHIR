package edu.gatech.i3l.omop.dao;

import edu.gatech.i3l.fhir.jpa.dao.BaseFhirDao;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import java.util.List;

import static java.lang.String.format;

/**
 * Created by rakesh.vidyadharan on 5/15/17.
 */
public class DAO {
    public static DAO getInstance() { return singleton; }

    public EntityManager getEntityManager() { return entityManager; }

    public <T extends BaseResourceEntity> T loadEntityById(final Class<T> cls, final Object primaryKey) {
        return entityManager.find(cls, primaryKey);
    }

    public <T extends BaseResourceEntity> T loadEntityBySource(final Class<T> cls, final String tableName, final String columnName, final String value) {
        final String query = format("SELECT t FROM %s t WHERE %s like :value", tableName, columnName);
        List<T> results = entityManager.createQuery(query, cls).setParameter("value", value).getResultList();
        return (results.isEmpty()) ? null : results.get(0);
    }

    public <T extends BaseResourceEntity> T loadEntityByLocation(final Class<T> cls, final String line1, final String line2,
                                                                 final String city, final String state, final String zipCode) {
        List<T> results;

        if (line2 != null) {
            final String query = "SELECT t FROM Location t WHERE address1 = :line1 AND address2 = :line2 AND city = :city AND state = :state AND zipCode = :zip";
            results = entityManager.createQuery(query, cls)
                    .setParameter("line1", line1)
                    .setParameter("line2", line2)
                    .setParameter("city", city)
                    .setParameter("state", state)
                    .setParameter("zip", zipCode)
                    .getResultList();
        } else {
            final String query = "SELECT t FROM Location t WHERE address1 = :line1 AND city = :city AND state = :state AND zipCode = :zip";
            results = entityManager.createQuery(query, cls)
                    .setParameter("line1", line1)
                    .setParameter("city", city)
                    .setParameter("state", state)
                    .setParameter("zip", zipCode)
                    .getResultList();
        }

        return (results.isEmpty()) ? null : results.get(0);
    }

    private DAO() {
        final String baseFhirDao = "myBaseDao";
        final WebApplicationContext myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
        entityManager = myAppCtx.getBean(baseFhirDao, BaseFhirDao.class).getEntityManager();
    }

    private final EntityManager entityManager;
    private static final DAO singleton = new DAO();
}
