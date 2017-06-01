package edu.gatech.i3l.omop.dao;

import edu.gatech.i3l.fhir.dstu2.entities.CareSite;
import edu.gatech.i3l.fhir.dstu2.entities.Location;
import edu.gatech.i3l.fhir.jpa.dao.BaseFhirDao;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by rakesh.vidyadharan on 5/11/17.
 */
public class CareSiteDAO {

    public static CareSiteDAO getInstance() {
        return singleton;
    }

    public Long getByNameAndLocation(CareSite organization, Location location) {
        if (organization == null) return null;

        final StringBuilder builder = new StringBuilder(128);
        builder.append("SELECT o.id FROM CareSite o where ");

        String name = organization.getCareSiteName();
        builder.append("o.careSiteName = :name");

        if (location != null) {
            builder.append(" AND o.location.id = :location");
        }

        TypedQuery<Long> query = DAO.getInstance().getEntityManager().createQuery(builder.toString(), Long.class);
        query.setParameter("name", name);
        if (location != null) query = query.setParameter("location", location.getId());

        List<Long> results = query.getResultList();
        return (results.isEmpty()) ? null : results.get(0);
    }

    private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(CareSiteDAO.class);
    private static final CareSiteDAO singleton = new CareSiteDAO();
}
