package edu.gatech.i3l.fhir.dstu2.entities;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import edu.gatech.i3l.fhir.jpa.entity.IResourceEntity;
import edu.gatech.i3l.omop.mapping.OmopConceptMapping;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "measurement")
public class OmopMeasurement extends BaseResourceEntity {

    private static final String RES_TYPE = "Observation";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "measurement_id", updatable = false)
    @Access(AccessType.PROPERTY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "person_id", nullable = false)
    @NotNull
    private PersonComplement person;

    @Column(name = "measurement_source_value")
    private String sourceValue;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "measurement_concept_id", nullable = false)
    @NotNull
    private Concept measurementConcept;

    @Column(name = "value_as_number")
    private Double valueAsNumber;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "value_as_concept_id")
    private Concept valueAsConcept;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_concept_id")
    private Concept unit;

    @Column(name = "range_low")
    private Double rangeLow;

    @Column(name = "range_high")
    private Double rangeHigh;

    @Column(name = "measurement_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date date;

    @Column(name = "measurement_time")
    // @Temporal(TemporalType.TIME)
    private String time;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_occurrence_id")
    private VisitOccurrence visitOccurrence;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_type_concept_id")
    private Concept type;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(PersonComplement person) {
        this.person = person;
    }

    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }

    public Concept getMeasurementConcept() {
        return measurementConcept;
    }

    public void setMeasurementConcept(Concept measurementConcept) {
        this.measurementConcept = measurementConcept;
    }

    public Double getValueAsNumber() {
        return valueAsNumber;
    }

    public void setValueAsNumber(Double valueAsNumber) {
        this.valueAsNumber = valueAsNumber;
    }

    public Concept getValueAsConcept() {
        return valueAsConcept;
    }

    public void setValueAsConcept(Concept valueAsConcept) {
        this.valueAsConcept = valueAsConcept;
    }

    public Concept getUnit() {
        return unit;
    }

    public void setUnit(Concept unit) {
        this.unit = unit;
    }

    public Double getRangeLow() {
        return rangeLow;
    }

    public void setRangeLow(Double rangeLow) {
        this.rangeLow = rangeLow;
    }

    public Double getRangeHigh() {
        return rangeHigh;
    }

    public void setRangeHigh(Double rangeHigh) {
        this.rangeHigh = rangeHigh;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public VisitOccurrence getVisitOccurrence() {
        return visitOccurrence;
    }

    public void setVisitOccurrence(VisitOccurrence visitOccurrence) {
        this.visitOccurrence = visitOccurrence;
    }

    public Concept getType() {
        return type;
    }

    public void setType(Concept type) {
        this.type = type;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IResource getRelatedResource() {
        final Observation observation = new Observation();
        observation.setId(id);
        observation.setPerson(person);
        if (valueAsNumber != null) observation.setValueAsNumber(new BigDecimal(valueAsNumber));
        if (valueAsConcept != null) observation.setValueAsConcept(valueAsConcept);
        if (rangeLow != null) observation.setRangeLow(new BigDecimal(rangeLow));
        if (rangeHigh != null) observation.setRangeHigh(new BigDecimal(rangeHigh));
        if (date != null) observation.setDate(date);
        if (type != null) observation.setType(type);

        final ca.uhn.fhir.model.dstu2.resource.Observation obs = (ca.uhn.fhir.model.dstu2.resource.Observation) observation.getRelatedResource();

        if (measurementConcept != null) {
            final String codeString = measurementConcept.getConceptCode();
            final String systemUriString = measurementConcept.getVocabulary().getReference();
            final String displayString = measurementConcept.getName();

            final CodeableConceptDt ccd = new CodeableConceptDt(systemUriString, codeString);
            ccd.getCodingFirstRep().setDisplay(displayString);
            obs.setCode(ccd);
        }

        return obs;
    }

    @Override
    public IResourceEntity constructEntityFromResource(IResource resource) {
        ca.uhn.fhir.model.dstu2.resource.Observation obs = (ca.uhn.fhir.model.dstu2.resource.Observation) resource;
        ResourceReferenceDt subjectReference = obs.getSubject();
        if (subjectReference != null) {
            if ("Patient".equals(subjectReference.getReference().getResourceType())) {
                PersonComplement person = PersonComplement.searchAndUpdate(subjectReference);
                if (person == null) return null; // We must have a patient
                this.setPerson(person);
            } else {
                // Group, Device, and Location are not supported in OMOP v5
                return null;
            }
        } else {
            // We must have subject reference.
            return null;
        }

        // We are writing to the database. Keep the source so we know where it is coming from
        OmopConceptMapping ocm = OmopConceptMapping.getInstance();
        if (obs.getId() != null) {
            // See if we already have this in the source field. If so,
            // then we want update not create
            OmopMeasurement origMeasurement = (OmopMeasurement) ocm.loadEntityBySource(OmopMeasurement.class, "OmopMeasurement", "sourceValue", obs.getId().getIdPart());
            if (origMeasurement == null)
                this.setSourceValue(obs.getId().getIdPart());
            else
                this.setId(origMeasurement.getId());
        }

        String code = obs.getCode().getCodingFirstRep().getCode();
        Long obsConceptRef = ocm.get(code);
        setMeasurementConcept(new Concept());
        if (obsConceptRef != null) {
            getMeasurementConcept().setId(obsConceptRef);
        } else {
            getMeasurementConcept().setId(0L);
            System.out.println("Measurement code not recognized: " + code + ". System: " + obs.getCode().getCodingFirstRep().getSystem());
        }

		/* Set the value of the observation */
        IDatatype value = obs.getValue();
        if (value instanceof QuantityDt) {
//			Long unitId = ocm.get(((QuantityDt) value).getUnit(), OmopConceptMapping.UCUM_CODE,
//					OmopConceptMapping.UCUM_CODE_STANDARD, OmopConceptMapping.UCUM_CODE_CUSTOM);
            String unitCode = ((QuantityDt) value).getCode();
            if (unitCode == null) {
                unitCode = ((QuantityDt) value).getUnit();
            }
            Long unitId = ocm.get(unitCode);
            this.valueAsNumber = ((QuantityDt) value).getValue().doubleValue();
            if (unitId != null) {
                this.unit = new Concept();
                this.unit.setId(unitId);
            }

            if (!obs.getReferenceRangeFirstRep().isEmpty())
                this.rangeHigh = obs.getReferenceRangeFirstRep().getHigh().getValue().doubleValue();
            if (!obs.getReferenceRangeFirstRep().isEmpty())
                this.rangeLow = obs.getReferenceRangeFirstRep().getLow().getValue().doubleValue();

        } else if (value instanceof CodeableConceptDt) {
//			Long valueAsConceptId = ocm.get(((CodeableConceptDt) value).getCodingFirstRep().getCode(),
//					OmopConceptMapping.CLINICAL_FINDING);
            Long valueAsConceptId = ocm.get(((CodeableConceptDt) value).getCodingFirstRep().getCode());
            if (valueAsConceptId != null) {
                this.valueAsConcept = new Concept();
                this.valueAsConcept.setId(valueAsConceptId);
            }
        }

        if (obs.getEffective() instanceof DateTimeDt) {
            this.date = ((DateTimeDt) obs.getEffective()).getValue();
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            this.time = timeFormat.format(((DateTimeDt) obs.getEffective()).getValue());
        } else if (obs.getEffective() instanceof PeriodDt) {
            // TODO: we need to handle period. We can probably use
            // we can use range_low and range_high. These are only available in Measurement
        }

		/* Set visit occurrence */
        Long visitOccurrenceId = obs.getEncounter().getReference().getIdPartAsLong();
        if (visitOccurrenceId != null) {
            this.visitOccurrence = new VisitOccurrence();
            this.visitOccurrence.setId(visitOccurrenceId);
        }

        CodeableConceptDt obsCategory = obs.getCategory();
        if (!obsCategory.isEmpty()) {
            List<CodingDt> catCodes = obsCategory.getCoding();
            for (CodingDt catCode : catCodes) {
                if (catCode.getCode().equalsIgnoreCase("exam")) {
                    setType(new Concept(44818701L));
                } else if (catCode.getCode().equalsIgnoreCase("laboratory")) {
                    setType(new Concept(4818702L));
                }
            }
        }
        return this;

    }


}
