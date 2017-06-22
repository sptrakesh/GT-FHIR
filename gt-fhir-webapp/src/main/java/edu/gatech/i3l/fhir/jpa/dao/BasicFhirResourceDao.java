package edu.gatech.i3l.fhir.jpa.dao;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.resource.Basic;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.dstu2.valueset.IssueSeverityEnum;
import ca.uhn.fhir.model.dstu2.valueset.IssueTypeEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.typesafe.config.Config;
import edu.gatech.i3l.fhir.dstu2.entities.*;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import edu.gatech.i3l.fhir.jpa.query.AbstractPredicateBuilder;
import edu.gatech.i3l.fhir.jpa.query.PredicateBuilder;
import edu.gatech.i3l.fhir.jpa.util.StopWatch;
import edu.gatech.i3l.omop.dao.ConceptDAO;
import org.hl7.fhir.instance.model.api.IIdType;

import static java.lang.String.format;

/**
 * Created by rakesh.vidyadharan on 6/20/17.
 */
public class BasicFhirResourceDao extends BaseFhirResourceDao<Basic> {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BasicFhirResourceDao.class);

    public BasicFhirResourceDao() {
        setResourceEntity(TherapyGroup.class);
        setValidateBean(true);
    }

    @Override
    public PredicateBuilder getPredicateBuilder() {
        return new AbstractPredicateBuilder() {};
    }

    @Override
    public DaoMethodOutcome create(final Basic resource, final String theIfNoneExist, final boolean thePerformIndexing) {
        final Class<Basic> resourceType = getResourceType();
        final ConceptDAO dao = ConceptDAO.getInstance();

        final StopWatch w = new StopWatch();
        final CodingDt coding = resource.getCode().getCodingFirstRep();
        final Config config = dao.config.getConfig("concept.basic");

        final Long id = dao.getConcept(coding);
        if (id == null) error();

        final Config therapyGroupComponentConfig = config.getConfig("therapyGroupComponent");
        final Config therapyGroupConfig = config.getConfig("therapyGroup");
        DaoMethodOutcome outcome = null;
        BaseResourceEntity entity = null;

        if (coding.getCode().equals(therapyGroupComponentConfig.getString("code"))) {
            entity = readTherapyGroupComponent(resource);
        } else if (coding.getCode().equals(therapyGroupConfig.getString("code"))) {
            entity = readTherapyGroup(resource);
        } else {
            error();
        }

        getBaseFhirDao().updateEntity(resource, entity, false, null, thePerformIndexing, true);
        outcome = toMethodOutcome(entity, resource).setCreated(true);
        getBaseFhirDao().notifyWriteCompleted();
        logger.info("Processed create on {} in {}ms", resourceType, w.getMillisAndRestart());
        return outcome;
    }

    @Override
    public BaseResourceEntity readEntity(IIdType theId, boolean theCheckForForcedId) {
        final Long pid = theId.getIdPartAsLong();
        BaseResourceEntity entity = getEntityManager().find(TherapyGroup.class, pid);
        if (entity == null) entity = getEntityManager().find(TherapyGroupComponent.class, pid);
        if (entity == null) {
            logger.warn("Unable to find entity with id: {} in therapy_group or therapy_group_component tables.", pid);
        }
        return entity;
    }

    @Override
    public DaoMethodOutcome delete(IIdType theId) {
        StopWatch w = new StopWatch();

        BaseResourceEntity entity = readEntity(theId, false);
        getEntityManager().remove(entity);

        getBaseFhirDao().notifyWriteCompleted();

        logger.info("Processed delete on {} in {}ms", theId.getValue(), w.getMillisAndRestart());
        return toMethodOutcome(entity, null);
    }

    private DaoMethodOutcome toMethodOutcome(final BaseResourceEntity entity, final IResource resource) {
        final DaoMethodOutcome outcome = new DaoMethodOutcome();

        outcome.setId(new IdDt(entity.getId()));
        outcome.setEntity(entity);

        if (resource != null) {
            resource.setId(new IdDt(entity.getId()));
            outcome.setResource(resource);
        }

        return outcome;
    }

    private TherapyGroup readTherapyGroup(final Basic basic) {
        final TherapyGroup group = new TherapyGroup();
        group.constructEntityFromResource(basic);
        return group;
    }

    private TherapyGroupComponent readTherapyGroupComponent(final Basic basic) {
        final TherapyGroupComponent group = new TherapyGroupComponent();
        group.constructEntityFromResource(basic);
        return group;
    }

    private void error() {
        error("Coding System Not Supported. We support TherapyGroup or TherapyGroupComponent code");
    }

    private void error(final String message) {
        OperationOutcome oo = new OperationOutcome();
        oo.addIssue().setSeverity(IssueSeverityEnum.ERROR).setCode(IssueTypeEnum.INVALID_CODE).setDetails(
                (new CodeableConceptDt()).setText(message));
        throw new UnprocessableEntityException(getContext(), oo);
    }
}
