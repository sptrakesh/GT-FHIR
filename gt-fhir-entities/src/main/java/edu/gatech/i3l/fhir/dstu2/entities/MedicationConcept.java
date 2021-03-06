package edu.gatech.i3l.fhir.dstu2.entities;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.NarrativeDt;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import edu.gatech.i3l.fhir.jpa.entity.IResourceEntity;

import javax.persistence.*;
import java.util.Date;

import static ca.uhn.fhir.model.dstu2.resource.Medication.SP_CODE;

@Entity
@Table(name = "concept")
public final class MedicationConcept extends BaseResourceEntity {

    private static final String RES_TYPE = "Medication";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concept_id", updatable = false)
    @Access(AccessType.PROPERTY)
    private Long id;

    @Column(name = "concept_name", updatable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "domain_id", referencedColumnName = "domain_id")
    private Domain domain;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "vocabulary_id", insertable = false, updatable = false)
    private Vocabulary vocabulary;

    @Column(name = "concept_class_id", updatable = false)
    private String conceptClassId;

    @Column(name = "standard_concept", updatable = false)
    private Character standardConcept;

    @Column(name = "concept_code", updatable = false)
    private String conceptCode;

    @Column(name = "valid_start_date", updatable = false)
    private Date validStartDate;

    @Column(name = "valid_end_date", updatable = false)
    private Date validEndDate;

    @Column(name = "invalid_reason", updatable = false)
    private String invalidReason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public String getConceptClassId() {
        return conceptClassId;
    }

    public void setConceptClassId(String conceptClassId) {
        this.conceptClassId = conceptClassId;
    }

    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    public Character getStandardConcept() {
        return standardConcept;
    }

    public void setStandardConcept(Character standardConcept) {
        this.standardConcept = standardConcept;
    }

    public String getConceptCode() {
        return conceptCode;
    }

    public void setConceptCode(String conceptCode) {
        this.conceptCode = conceptCode;
    }

    public Date getValidStartDate() {
        return validStartDate;
    }

    public void setValidStartDate(Date validStartDate) {
        this.validStartDate = validStartDate;
    }

    public Date getValidEndDate() {
        return validEndDate;
    }

    public void setValidEndDate(Date validEndDate) {
        this.validEndDate = validEndDate;
    }

    public String getInvalidReason() {
        return invalidReason;
    }

    public void setInvalidReason(String invalidReason) {
        this.invalidReason = invalidReason;
    }

    @Override
    public String toString() {
        //Since this is an omop v.5 based model, all the information below is expected to be not null.
        return this.getId() + ", "
                + this.getName() + ", "
                + this.getDomain() + ", "
                + this.getConceptClassId() + ", "
                + this.getStandardConcept() + ", "
                + this.getVocabulary().getId() + ", "
                + this.getConceptCode() + ", "
                + this.getValidStartDate() + ", "
                + this.getValidEndDate();
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String translateSearchParam(String theSearchParam) {
        switch (theSearchParam) {
            case SP_CODE:
                return "conceptCode";
            default:
                break;
        }
        return theSearchParam;
    }

    @Override
    public IResource getRelatedResource() {
        Medication resource = new Medication();
        resource.setId(new IdDt(this.getId()));
        CodeableConceptDt code = new CodeableConceptDt(this.getVocabulary().getSystemUri(), this.getConceptCode());
        code.getCodingFirstRep().setDisplay(this.getName());
        resource.setCode(code);
        NarrativeDt narrative = new NarrativeDt();
        narrative.setDiv(this.toString());
        resource.setText(narrative);
        return resource;
    }

    /**
     * This entity relates to a omop concept registry and is not supposed to be created/updated.
     */
    //TODO somehow we should be able to send message back in an OperationOutcome
    @Override
    public IResourceEntity constructEntityFromResource(IResource resource) {
        return null;
    }

}
