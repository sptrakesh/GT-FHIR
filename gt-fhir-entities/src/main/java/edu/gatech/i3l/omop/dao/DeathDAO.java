package edu.gatech.i3l.omop.dao;

import edu.gatech.i3l.fhir.dstu2.entities.Death;

import javax.persistence.EntityManager;

/**
 * Created by rakesh.vidyadharan on 5/18/17.
 */
public class DeathDAO {

    public static DeathDAO getInstance() {
        return singleton;
    }

    public Death getById(final Long id) {
        if (id == null) return null;
        final EntityManager em = DAO.getInstance().getEntityManager();
        return em.find(Death.class, id);
    }

    public void remove(final Long id) {
        final Death death = getById(id);
        if (death != null) {
            DAO.getInstance().getEntityManager().remove(death);
        }
    }

    private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(DeathDAO.class);
    private static final DeathDAO singleton = new DeathDAO();
}
