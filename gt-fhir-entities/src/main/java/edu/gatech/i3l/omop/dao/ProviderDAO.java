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

    public static ProviderDAO getInstance() {
        return singleton;
    }

    public Long getByNameAndLocation(final Provider provider, final Location location) {
        if (provider == null) return null;

        final StringBuilder builder = new StringBuilder(128);
        builder.append("SELECT p.id FROM Provider p where ");

        String name = provider.getProviderName();
        builder.append("p.providerName = :name");

        if (location != null && provider.getCareSite() != null) {
            builder.append(" AND p.careSite.location.id = :location");
        }

        TypedQuery<Long> query = DAO.getInstance().getEntityManager().createQuery(builder.toString(), Long.class);
        query.setParameter("name", name);
        if (location != null) query = query.setParameter("location", location.getId());

        List<Long> results = query.getResultList();
        return (results.isEmpty()) ? null : results.get(0);
    }

    private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(ProviderDAO.class);
    private static final ProviderDAO singleton = new ProviderDAO();
}
