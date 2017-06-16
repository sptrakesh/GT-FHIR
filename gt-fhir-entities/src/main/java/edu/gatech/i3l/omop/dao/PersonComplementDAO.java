package edu.gatech.i3l.omop.dao;

import edu.gatech.i3l.fhir.dstu2.entities.Location;
import edu.gatech.i3l.fhir.dstu2.entities.PersonComplement;
import edu.gatech.i3l.fhir.dstu2.entities.keys.PersonIdentifierPK;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by rakesh.vidyadharan on 5/12/17.
 */
public class PersonComplementDAO {

    public static PersonComplementDAO getInstance() {
        return singleton;
    }

    public Long getByNameAndLocation(PersonComplement person, Location location) {
        if (person == null) return null;

        final StringBuilder builder = new StringBuilder(128);
        builder.append("SELECT p.id FROM PersonComplement p where ");

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

        TypedQuery<Long> query = DAO.getInstance().getEntityManager().createQuery(builder.toString(), Long.class);
        if (StringUtils.isNotBlank(family_name)) query = query.setParameter("fname", family_name);
        if (StringUtils.isNotBlank(given1_name)) query = query.setParameter("gname1", given1_name);
        if (StringUtils.isNotBlank(given2_name)) query = query.setParameter("gname2", given2_name);
        if (location != null) query = query.setParameter("location", location.getId());

        List<Long> results = query.getResultList();
        return (results.isEmpty()) ? null : results.get(0);
    }

    public edu.gatech.i3l.fhir.dstu2.entities.PersonIdentifier getPersonIdentifier(final PersonComplement person, final String system, final String value) {
        final PersonIdentifierPK pk = new PersonIdentifierPK(person, system, value);
        return DAO.getInstance().getEntityManager().find(edu.gatech.i3l.fhir.dstu2.entities.PersonIdentifier.class, pk);
    }

    private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(PersonComplementDAO.class);
    private static final PersonComplementDAO singleton = new PersonComplementDAO();
}
