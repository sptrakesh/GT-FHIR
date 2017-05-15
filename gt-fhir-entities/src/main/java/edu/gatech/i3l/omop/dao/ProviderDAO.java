package edu.gatech.i3l.omop.dao;

import edu.gatech.i3l.fhir.dstu2.entities.CareSite;
import edu.gatech.i3l.fhir.dstu2.entities.Location;
import edu.gatech.i3l.fhir.dstu2.entities.Provider;
import edu.gatech.i3l.fhir.jpa.dao.BaseFhirDao;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by rakesh.vidyadharan on 5/12/17.
 */
public class ProviderDAO {
    private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(ProviderDAO.class);

    private static final ProviderDAO singleton = new ProviderDAO();
    private final EntityManager entityManager;

    private ProviderDAO() {
        String baseFhirDao = "myBaseDao";
        WebApplicationContext myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
        entityManager = myAppCtx.getBean(baseFhirDao, BaseFhirDao.class).getEntityManager();
    }

    public static ProviderDAO getInstance() {
        return singleton;
    }

    public Long getByNameAndLocation(final Provider provider, final Location location) {
        if (provider == null) return null;

        final StringBuilder builder = new StringBuilder(128);
        builder.append("SELECT p FROM Provider p where ");

        String name = provider.getProviderName();
        builder.append("p.providerName = :name");

        if (location != null && provider.getCareSite() != null) {
            builder.append(" AND p.careSite.location.id = :location");
        }

        TypedQuery<? extends BaseResourceEntity> query = entityManager.createQuery(builder.toString(), Provider.class);
        query.setParameter("name", name);
        if (location != null) query = query.setParameter("location", location.getId());

        List<? extends BaseResourceEntity> results = query.getResultList();
        if (results.size() > 0) {
            Provider pr = (Provider) results.get(0);
            return pr.getId();
        } else return null;
    }
}
