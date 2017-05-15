package edu.gatech.i3l.omop.dao;

import edu.gatech.i3l.fhir.dstu2.entities.Location;
import edu.gatech.i3l.fhir.dstu2.entities.PersonComplement;
import edu.gatech.i3l.fhir.jpa.dao.BaseFhirDao;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by rakesh.vidyadharan on 5/12/17.
 */
public class PersonComplementDAO {
    private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(PersonComplementDAO.class);

    private static final PersonComplementDAO singleton = new PersonComplementDAO();
    private final EntityManager entityManager;

    private PersonComplementDAO() {
        String baseFhirDao = "myBaseDao";
        WebApplicationContext myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
        entityManager = myAppCtx.getBean(baseFhirDao, BaseFhirDao.class).getEntityManager();
    }

    public static PersonComplementDAO getInstance() {
        return singleton;
    }

    public Long getByNameAndLocation(PersonComplement person, Location location) {
        if (person == null) return null;

        final StringBuilder builder = new StringBuilder(128);
        builder.append("SELECT p FROM PersonComplement p where ");

        String family_name = person.getFamilyName();
        String given1_name = person.getGivenName1();
        String given2_name = person.getGivenName2();

        // Construct where clause here.
        boolean addAnd = false;
        if (StringUtils.isNotBlank(family_name)) {
            builder.append("p.familyName = :fname");
            addAnd = true;
        }

        if (StringUtils.isNotBlank(given1_name)) {
            if (!addAnd) builder.append("p.givenName1 LIKE :gname1");
            else builder.append(" AND p.givenName1 LIKE :gname1");
        }
        if (StringUtils.isNotBlank(given2_name)) {
            if (!addAnd) builder.append("p.givenName2 LIKE :gname2");
            else builder.append(" AND p.givenName2 LIKE :gname2");
        }

        if (location != null) {
            if (!addAnd) builder.append("p.location.id = :location");
            else builder.append(" AND p.location.id = :location");
        }

        System.out.println("Query for Person: " + builder);

        TypedQuery<? extends BaseResourceEntity> query = entityManager.createQuery(builder.toString(), PersonComplement.class);
        if (StringUtils.isNotBlank(family_name)) query = query.setParameter("fname", family_name);
        if (StringUtils.isNotBlank(given1_name)) query = query.setParameter("gname1", given1_name);
        if (StringUtils.isNotBlank(given2_name)) query = query.setParameter("gname2", given2_name);
        if (location != null) query = query.setParameter("location", location.getId());

        if (location != null) {
            System.out.println("family:" + family_name + " gname1:" + given1_name + " gname2:" + given2_name + " location:" + location.getId());
        } else {
            System.out.println("family:" + family_name + " gname1:" + given1_name + " gname2:" + given2_name);
        }
        List<? extends BaseResourceEntity> results = query.getResultList();
        if (results.size() > 0) {
            PersonComplement person_c = (PersonComplement) results.get(0);
            return person_c.getId();
        } else return null;
    }
}
