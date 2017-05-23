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
        builder.append("SELECT o FROM CareSite o where ");

        String name = organization.getCareSiteName();
        builder.append("o.careSiteName = :name");

        if (location != null) {
            builder.append(" AND o.location.id = :location");
        }

        TypedQuery<? extends BaseResourceEntity> query = DAO.getInstance().getEntityManager().createQuery(builder.toString(), CareSite.class);
        query.setParameter("name", name);
        if (location != null) query = query.setParameter("location", location.getId());

        List<? extends BaseResourceEntity> results = query.getResultList();
        if (results.size() > 0) {
            CareSite cs = (CareSite) results.get(0);
            return cs.getId();
        } else return null;
    }

    private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(CareSiteDAO.class);
    private static final CareSiteDAO singleton = new CareSiteDAO();
}
