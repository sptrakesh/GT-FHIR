package edu.gatech.i3l.fhir.dstu2.entities;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration.Dosage;
import ca.uhn.fhir.model.dstu2.resource.MedicationDispense;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import edu.gatech.i3l.fhir.jpa.entity.IResourceEntity;
import edu.gatech.i3l.omop.dao.ConceptDAO;
import edu.gatech.i3l.omop.dao.DAO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@DiscriminatorValue("DrugStatement")
public final class DrugExposureStatement extends DrugExposure {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DrugExposureStatement.class);

    public static final String RES_TYPE = "MedicationStatement";
    public static final String RX_NORM = "RXNORM";
    public static final String RX_NORM_SYSTEM = "http://www.nlm.nih.gov/research/umls/rxnorm";

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "drug_type_concept_id")
    private Concept drugExposureType;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "person_id", nullable = false)
    @NotNull
    private PersonComplement person;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "visit_occurrence_id")
    private VisitOccurrence visitOccurrence;

    @Column(name = "drug_exposure_start_date")
    private Date startDate;

    @Column(name = "drug_exposure_end_date")
    private Date endDate;

    @Column(name = "stop_reason")
    private String stopReason;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "days_supply")
    private Integer daysSupply;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "drug_concept_id")
    @NotNull
    private Concept medication;

    public PersonComplement getPerson() { return person; }

    public void setPerson(PersonComplement person) { this.person = person; }

    public VisitOccurrence getVisitOccurrence() { return visitOccurrence; }

    public void setVisitOccurrence(VisitOccurrence visitOccurrence) { this.visitOccurrence = visitOccurrence; }

    public String getStopReason() {
        return stopReason;
    }

    public void setStopReason(String stopReason) {
        this.stopReason = stopReason;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Integer getDaysSupply() {
        return daysSupply;
    }

    public void setDaysSupply(Integer daysSupply) {
        this.daysSupply = daysSupply;
    }

    public Concept getMedication() {
        return medication;
    }

    public void setMedication(Concept medication) {
        this.medication = medication;
    }

    public Concept getDrugExposureType() {
        return drugExposureType;
    }

    public void setDrugExposureType(Concept drugExposureType) {
        this.drugExposureType = drugExposureType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
            case MedicationDispense.SP_PATIENT:
                return "person";
            case MedicationDispense.SP_MEDICATION:
                return "medication";
            default:
                break;
        }
        return theSearchParam;
    }

    @Override
    public IResource getRelatedResource() {
        MedicationStatement resource = new MedicationStatement();
        resource.setId(this.getIdDt());
        resource.setPatient(new ResourceReferenceDt(person.getIdDt()));

        if (visitOccurrence != null) {
            final List<ResourceReferenceDt> encounter = new ArrayList<>();
            encounter.add(new ResourceReferenceDt(visitOccurrence.getIdDt()));
            resource.setSupportingInformation(encounter);
        }

        // Adding medication to Contained.
        CodingDt medCoding = new CodingDt(this.getMedication().getVocabulary().getSystemUri(), this.getMedication().getConceptCode());
        medCoding.setDisplay(this.getMedication().getName());

        List<CodingDt> codingList = new ArrayList<CodingDt>();
        codingList.add(medCoding);
        CodeableConceptDt codeDt = new CodeableConceptDt();
        codeDt.setCoding(codingList);

        resource.setMedication(codeDt);

        if (startDate != null) {
            if (endDate != null) {
                final PeriodDt period = new PeriodDt();
                period.setStart(this.startDate, TemporalPrecisionEnum.DAY);
                period.setEnd(this.endDate, TemporalPrecisionEnum.DAY);
                resource.setEffective(period);
            } else {
                final DateTimeDt start = new DateTimeDt(startDate);
                start.setPrecision(TemporalPrecisionEnum.DAY);
                resource.setEffective(start);
            }
        }

        return resource;
    }

    @Override
    public IResourceEntity constructEntityFromResource(IResource resource) {
        if (!(resource instanceof MedicationStatement)) return null;

        final MedicationStatement medicationStatement = (MedicationStatement) resource;

        drugExposureType = DAO.getInstance().getEntityManager().find(Concept.class, 38000178L);

        if (medicationStatement.getMedication() instanceof CodeableConceptDt) {
            final CodeableConceptDt ccd = (CodeableConceptDt) medicationStatement.getMedication();
            final CodingDt cd = ccd.getCodingFirstRep();
            switch (cd.getSystem()) {
                case RX_NORM:
                case RX_NORM_SYSTEM:
                    medication = new Concept(ConceptDAO.getInstance().getConcept(cd.getCode(),RX_NORM));
                    break;
                default:
                    logger.warn("Unsupported medication system: {} with code: {}", cd.getSystem(), cd.getCode());
            }
        } else if (medicationStatement.getMedication() instanceof ResourceReferenceDt) {
            final ResourceReferenceDt medicationRef = (ResourceReferenceDt) medicationStatement.getMedication();
            final Long medicationRefId = medicationRef.getReference().getIdPartAsLong();
            if (medicationRef != null) {
                medication = new Concept(medicationRefId);
            }
        }

        if (medicationStatement.getPatient() != null) {
            person = PersonComplement.searchAndUpdate(medicationStatement.getPatient());
        }

        if (medicationStatement.getSupportingInformation()!= null)
        {
            for (final ResourceReferenceDt ref : medicationStatement.getSupportingInformation()) {
                switch (ref.getReference().getResourceType()) {
                    case VisitOccurrence.RES_TYPE:
                        visitOccurrence = new VisitOccurrence();
                        visitOccurrence.setId(ref.getReference().getIdPartAsLong());
                        break;
                    default:
                        logger.warn("Unsupported supportingInformation reference {}", ref.getReference().getResourceType());
                }
            }
        }

        final IDatatype effective = medicationStatement.getEffective();
        if (effective != null)
        {
            if (effective instanceof DateTimeDt) {
                this.startDate = ((DateTimeDt) effective).getValue();
            } else if (effective instanceof PeriodDt) {
                final PeriodDt period = (PeriodDt) effective;
                this.startDate = period.getStart();
                this.endDate = period.getEnd();
            }
        }

        return this;
    }
}
