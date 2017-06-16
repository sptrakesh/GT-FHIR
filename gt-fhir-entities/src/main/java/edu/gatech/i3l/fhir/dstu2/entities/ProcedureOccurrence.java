package edu.gatech.i3l.fhir.dstu2.entities;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import edu.gatech.i3l.fhir.jpa.entity.IResourceEntity;
import edu.gatech.i3l.omop.dao.ConceptDAO;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

import static ca.uhn.fhir.model.dstu2.resource.Procedure.SP_ENCOUNTER;
import static ca.uhn.fhir.model.dstu2.resource.Procedure.SP_PATIENT;

@Entity
@Table(name = "procedure_occurrence")
public class ProcedureOccurrence extends BaseResourceEntity {

    private static final String RES_TYPE = "Procedure";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "procedure_occurrence_id", updatable = false)
    @Access(AccessType.PROPERTY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "person_id", nullable = false, foreignKey = @ForeignKey(name = "fpk_procedure_person"))
    @NotNull
    private PersonComplement person;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "procedure_concept_id", foreignKey = @ForeignKey(name = "fpk_procedure_concept"))
    private Concept procedureConcept;

    @Column(name = "procedure_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date date;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "procedure_type_concept_id", foreignKey = @ForeignKey(name = "fpk_procedure_type_concept"))
    private Concept procedureTypeConcept;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "modifier_concept_id", foreignKey = @ForeignKey(name = "fpk_procedure_modifier"))
    private Concept modifierConcept;

    @Column(name = "quantity")
    private Long quantity;

    @ManyToOne   // (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", foreignKey = @ForeignKey(name = "fpk_procedure_provider"))
    private Provider provider;

    @ManyToOne
    @JoinColumn(name = "visit_occurrence_id", foreignKey = @ForeignKey(name = "fpk_procedure_visit"))
    private VisitOccurrence visitOccurrence;

    @Column(name = "procedure_source_value")
    private String procedureSourceValue;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "procedure_source_concept_id", foreignKey = @ForeignKey(name = "fpk_procedure_concept_s"))
    private Concept procedureSourceConcept;

    @Column(name = "qualifier_source_value")
    private String qualifierSourceValue;

    public ProcedureOccurrence() {
        super();
    }

    public ProcedureOccurrence(Long id, PersonComplement person, Concept procedureConcept, Date date, Concept procedureTypeConcept,
                               Concept modifierConcept, Long quantity, Provider provider, VisitOccurrence visitOccurrence,
                               String procedureSourceValue, Concept procedureSourceConcept, String qualifierSourceValue) {
        super();
        this.id = id;
        this.person = person;
        this.procedureConcept = procedureConcept;
        this.date = date;
        this.procedureTypeConcept = procedureTypeConcept;
        this.modifierConcept = modifierConcept;
        this.quantity = quantity;
        this.provider = provider;
        this.visitOccurrence = visitOccurrence;
        this.procedureSourceValue = procedureSourceValue;
        this.procedureSourceConcept = procedureSourceConcept;
        this.qualifierSourceValue = qualifierSourceValue;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PersonComplement getPerson() {
        return person;
    }

    public void setPerson(PersonComplement person) {
        this.person = person;
    }

    public Concept getProcedureConcept() {
        return procedureConcept;
    }

    public void setProcedureConcept(Concept procedureConcept) {
        this.procedureConcept = procedureConcept;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Concept getProcedureTypeConcept() {
        return procedureTypeConcept;
    }

    public void setProcedureTypeConcept(Concept procedureTypeConcept) {
        this.procedureTypeConcept = procedureTypeConcept;
    }

    public Concept getModifierConcept() {
        return modifierConcept;
    }

    public void setModifierConcept(Concept modifierConcept) {
        this.modifierConcept = modifierConcept;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public VisitOccurrence getVisitOccurrence() {
        return visitOccurrence;
    }

    public void setVisitOccurrence(VisitOccurrence visitOccurrence) {
        this.visitOccurrence = visitOccurrence;
    }

    public String getProcedureSourceValue() {
        return procedureSourceValue;
    }

    public void setProcedureSourceValue(String procedureSourceValue) {
        this.procedureSourceValue = procedureSourceValue;
    }

    public Concept getProcedureSourceConcept() {
        return procedureSourceConcept;
    }

    public void setProcedureSourceConcept(Concept procedureSourceConcept) {
        this.procedureSourceConcept = procedureSourceConcept;
    }

    public String getQualifierSourceValue() {
        return qualifierSourceValue;
    }

    public void setQualifierSourceValue(String qualifierSourceValue) {
        this.qualifierSourceValue = qualifierSourceValue;
    }


    @Override
    public FhirVersionEnum getFhirVersion() {
        // TODO Auto-generated method stub
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
            case SP_PATIENT:
                return "person";
            case SP_ENCOUNTER:
                return "visitOccurrence";
            default:
                break;
        }
        return theSearchParam;
    }

    @Override
    public IResource getRelatedResource() {
        Procedure procedure = new Procedure();

        procedure.setId(this.getIdDt());

        // Set patient
        ResourceReferenceDt patientReference = new ResourceReferenceDt(new IdDt(person.getResourceType(), person.getId()));
        final StringBuilder patientName = new StringBuilder(128);
        if (person.getGivenName1() != null && !person.getGivenName1().isEmpty()) {
            patientName.append(person.getGivenName1());
        }
        if (person.getGivenName2() != null && !person.getGivenName2().isEmpty()) {
            patientName.append(" ").append(person.getGivenName2());
        }
        if (person.getFamilyName() != null && !person.getFamilyName().isEmpty()) {
            patientName.append(" ").append(person.getFamilyName());
        }
        patientReference.setDisplay(patientName.toString());
        procedure.setSubject(patientReference);

        if (visitOccurrence != null) {
            procedure.setEncounter(new ResourceReferenceDt(new IdDt(visitOccurrence.getResourceType(), visitOccurrence.getId())));
        }

        //TODO: revisit this. For now just set to completed
        procedure.setStatus(ProcedureStatusEnum.COMPLETED);

        {
            final CodeableConceptDt procedureCodeConcept = new CodeableConceptDt();
            final CodingDt coding = new CodingDt(
                    ConceptDAO.getInstance().getSystemUri(procedureConcept.getVocabulary().getName()), procedureConcept.getConceptCode());
            coding.setDisplay(procedureConcept.getName());
            procedureCodeConcept.addCoding(coding);
            procedure.setCode(procedureCodeConcept);

            // FHIR does not require the coding. If our System URI is not mappable
            // from OMOP database, then coding would be empty. Set Text here. Even text
            // is not required in FHIR. But, then no reason to have this condition, I think...
            String theText = procedureConcept.getName() + ", " + procedureConcept.getVocabulary().getName() + ", "
                    + procedureConcept.getConceptCode();
            procedureCodeConcept.setText(theText);
        }

        if (procedureTypeConcept != null) {
            final CodeableConceptDt type = new CodeableConceptDt();
            final CodingDt coding = new CodingDt(
                    ConceptDAO.getInstance().getSystemUri(procedureTypeConcept.getVocabulary().getName()), procedureTypeConcept.getConceptCode());
            coding.setDisplay(procedureTypeConcept.getName());
            type.addCoding(coding);
            procedure.setCategory(type);
        }

        DateTimeDt dateDt = new DateTimeDt(date);
        procedure.setPerformed(dateDt);

        return procedure;
    }

    @Override
    public IResourceEntity constructEntityFromResource(IResource resource) {
        if (!(resource instanceof Procedure)) return null;
        final Procedure procedure = (Procedure) resource;

        if (procedure.getSubject() != null) {
            person = PersonComplement.searchAndUpdate(procedure.getSubject());
        }

        {
            final CodingDt code = procedure.getCode().getCodingFirstRep();
            procedureConcept = new Concept(ConceptDAO.getInstance().getConcept(code.getCode(), code.getSystem()));
        }

        if (procedure.getCategory() != null) {
            final CodingDt category = procedure.getCategory().getCodingFirstRep();
            final Long conceptId = ConceptDAO.getInstance().getConcept(category.getCode(), category.getSystem());
            if (conceptId != null ) procedureTypeConcept = new Concept(conceptId);
        }

        if (procedure.getPerformed() != null) {
            if (procedure.getPerformed() instanceof DateTimeDt) {
                date = ((DateTimeDt) procedure.getPerformed()).getValue();
            }
            else if (procedure.getPerformed() instanceof PeriodDt) {
                // TODO: OMOP does not support periods
            }
        }

        if (procedure.getEncounter() != null) {
            visitOccurrence = new VisitOccurrence();
            visitOccurrence.setId(procedure.getEncounter().getReference().getIdPartAsLong());
        }

        if (procedure.getPerformer() != null && !procedure.getPerformer().isEmpty()) {
            final IBaseResource res = procedure.getPerformer().get(0).getActor().getResource();
            if (res instanceof Practitioner) {
                provider = new Provider();
                provider.setId(((Practitioner) res).getId().getIdPartAsLong());
            }
        }

        return this;
    }

}
