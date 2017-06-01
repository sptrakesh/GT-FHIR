package edu.gatech.i3l.omop.dao;

import edu.gatech.i3l.fhir.jpa.dao.BaseFhirDao;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;

/**
 * Created by rakesh.vidyadharan on 5/15/17.
 */
public class DAO {
    public static DAO getInstance() { return singleton; }

    public EntityManager getEntityManager() { return entityManager; }

    public <T extends BaseResourceEntity> T loadEntityById(final Class<T> cls, final Object primaryKey) {
        return entityManager.find(cls, primaryKey);
    }

    private DAO() {
        final String baseFhirDao = "myBaseDao";
        final WebApplicationContext myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
        entityManager = myAppCtx.getBean(baseFhirDao, BaseFhirDao.class).getEntityManager();
    }

    private final EntityManager entityManager;
    private static final DAO singleton = new DAO();
}
