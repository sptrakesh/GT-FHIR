package edu.gatech.i3l.fhir.dstu2.entities;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Basic;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.IntegerDt;
import com.typesafe.config.Config;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import edu.gatech.i3l.fhir.jpa.entity.IResourceEntity;
import edu.gatech.i3l.omop.dao.ConceptDAO;
import edu.gatech.i3l.omop.dao.DAO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static java.lang.String.format;

/**
 * Created by rakesh.vidyadharan on 6/20/17.
 */
@Entity
@Table(name = "fhir_therapy_group_component")
public class TherapyGroupComponent extends BaseResourceEntity {

    private static final String RES_TYPE = "Basic";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "therapy_id", updatable = false)
    @Access(AccessType.PROPERTY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "therapy_group_id", nullable = false, foreignKey = @ForeignKey(name = "fpk_therapy_group_component_therapy_group"))
    @NotNull
    private TherapyGroup therapyGroup;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "drug_exposure_id", foreignKey = @ForeignKey(name = "fpk_therapy_group_drug_exposure"))
    private DrugExposureStatement drugExposureStatement;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "procedure_occurrence_id", foreignKey = @ForeignKey(name = "fpk_therapy_group_procedure_occurence"))
    private ProcedureOccurrence procedureOccurrence;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "observation_id", foreignKey = @ForeignKey(name = "fpk_therapy_group_observation"))
    private Observation observation;

    @Column(name = "therapy_group_sequence_id")
    private Integer sequence;

    @Override
    public IResource getRelatedResource() {
        final Basic basic = new Basic();
        basic.setId(new IdDt(id));
        basic.setSubject(new ResourceReferenceDt(therapyGroup.getIdDt()));

        writeCode(basic);
        writeExtensions(basic);

        return basic;
    }

    @Override
    public IResourceEntity constructEntityFromResource(IResource resource) {
        if (!(resource instanceof Basic)) return null;

        final Basic basic = (Basic) resource;
        readTherapyGroup(basic);
        readExtensions(basic);
        return this;
    }

    @Override
    public String translateSearchParam(String theSearchParam) {
        return null;
    }

    @Override
    public FhirVersionEnum getFhirVersion() {
        return FhirVersionEnum.DSTU2;
    }

    @Override
    public String getResourceType() {
        return RES_TYPE;
    }

    @Override
    public InstantDt getUpdated() {
        return null;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public TherapyGroup getTherapyGroup() {
        return therapyGroup;
    }

    public void setTherapyGroup(TherapyGroup therapyGroup) {
        this.therapyGroup = therapyGroup;
    }

    public DrugExposureStatement getDrugExposureStatement() {
        return drugExposureStatement;
    }

    public void setDrugExposureStatement(DrugExposureStatement drugExposureStatement) {
        this.drugExposureStatement = drugExposureStatement;
    }

    public ProcedureOccurrence getProcedureOccurrence() {
        return procedureOccurrence;
    }

    public void setProcedureOccurrence(ProcedureOccurrence procedureOccurrence) {
        this.procedureOccurrence = procedureOccurrence;
    }

    public Observation getObservation() {
        return observation;
    }

    public void setObservation(Observation observation) {
        this.observation = observation;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    private void writeCode(final Basic basic) {
        final ConceptDAO dao = ConceptDAO.getInstance();
        final Config config = ConceptDAO.getInstance().config.getConfig("concept.basic.therapyGroupComponent");
        final String url = dao.getSystemUri(config.getString("url"));

        final CodingDt code = new CodingDt(url, config.getString("code"));
        final CodeableConceptDt ccdt = new CodeableConceptDt();
        ccdt.addCoding(code);
        basic.setCode(ccdt);
    }

    private void writeExtensions(final Basic basic) {
        if ((drugExposureStatement == null) && (procedureOccurrence == null) && (observation == null)) return;

        final Config config = ConceptDAO.getInstance().config.getConfig("entities.Basic.TherapyGroupComponent");

        if (drugExposureStatement != null) {
            basic.addUndeclaredExtension(createExtension(drugExposureStatement, "MedicationStatement", config));
        }

        if (procedureOccurrence != null) {
            basic.addUndeclaredExtension(createExtension(procedureOccurrence, "Procedure", config));
        }

        if (observation != null) {
            basic.addUndeclaredExtension(createExtension(observation, "Observation", config));
        }

        if (sequence != null) {
            final ExtensionDt ext = new ExtensionDt();
            ext.setUrl(config.getString("sequenceUrl"));
            ext.setValue(new IntegerDt(sequence));
            basic.addUndeclaredExtension(ext);
        }
    }

    private ExtensionDt createExtension(final BaseResourceEntity entity, final String entityName, final Config config) {
        final ExtensionDt edt = new ExtensionDt();
        edt.setUrl(config.getString(format("%s.url", entityName)));
        edt.setValue(new ResourceReferenceDt(entity.getIdDt()));

        return edt;
    }

    private void readTherapyGroup(final Basic basic) {
        final ResourceReferenceDt subject = basic.getSubject();
        therapyGroup = DAO.getInstance().loadEntityById(TherapyGroup.class, subject.getReference().getIdPartAsLong());
        if (therapyGroup == null)
        {
            throw new IllegalArgumentException(format("Required TherapyGroup reference (%s) not found", subject.getReference().getIdPartAsLong()));
        }
    }

    private void readExtensions(final Basic basic) {
        final Config config = ConceptDAO.getInstance().config.getConfig("entities.Basic.TherapyGroupComponent");

        for (final ExtensionDt extension : basic.getAllUndeclaredExtensions()) {
            if (extension.getUrl().equals(config.getString("MedicationStatement.url"))) {
                final ResourceReferenceDt ref = (ResourceReferenceDt) extension.getValue();
                drugExposureStatement = DAO.getInstance().loadEntityById(DrugExposureStatement.class, ref.getReference().getIdPartAsLong());
            } else if (extension.getUrl().equals(config.getString("Procedure.url"))) {
                final ResourceReferenceDt ref = (ResourceReferenceDt) extension.getValue();
                procedureOccurrence = DAO.getInstance().loadEntityById(ProcedureOccurrence.class, ref.getReference().getIdPartAsLong());
            } else if (extension.getUrl().equals(config.getString("Observation.url"))) {
                final ResourceReferenceDt ref = (ResourceReferenceDt) extension.getValue();
                observation = DAO.getInstance().loadEntityById(Observation.class, ref.getReference().getIdPartAsLong());
            } else if (extension.getUrl().equals(config.getString("sequenceUrl"))) {
                sequence = ((IntegerDt) extension.getValue()).getValue();
            } else {
                throw new UnsupportedOperationException(format("Unsupported extension uri: %s", extension.getUrl()));
            }
        }
    }
}
