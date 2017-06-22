package edu.gatech.i3l.fhir.dstu2.entities;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.valueset.ObservationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import edu.gatech.i3l.fhir.jpa.entity.IResourceEntity;
import edu.gatech.i3l.omop.dao.ConceptDAO;
import edu.gatech.i3l.omop.dao.DAO;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "observation")
public class Observation extends BaseResourceEntity {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Observation.class);

    private static final String RES_TYPE = "Observation";
    private static final ObservationStatusEnum STATUS = ObservationStatusEnum.FINAL;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "observation_id", updatable = false)
    @Access(AccessType.PROPERTY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false, foreignKey = @ForeignKey(name = "fpk_observation_person"))
    private PersonComplement person;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "observation_concept_id", nullable = false, foreignKey = @ForeignKey(name = "fpk_observation_concept"))
    private Concept observationConcept;

    @Column(name = "observation_date")
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "observation_time")
    private String time;

    @Column(name = "value_as_string")
    private String valueAsString;

    @Column(name = "value_as_number")
    private Double valueAsNumber;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "value_as_concept_id", foreignKey = @ForeignKey(name = "fpk_observation_value"))
    private Concept valueAsConcept;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "observation_type_concept_id", foreignKey = @ForeignKey(name = "fpk_observation_type_concept"))
    private Concept type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", foreignKey = @ForeignKey(name = "fpk_observation_provider"))
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_occurrence_id", foreignKey = @ForeignKey(name = "fpk_observation_visit"))
    private VisitOccurrence visitOccurrence;

    @Column(name = "observation_source_value")
    private String sourceValue;

    @ManyToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_concept_id", foreignKey = @ForeignKey(name = "fpk_observation_unit_concept"))
    private Concept unit;

    @Column(name = "unit_source_value")
    private String unitSourceValue;

    public Observation() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public PersonComplement getPerson() {
        return person;
    }

    public void setPerson(PersonComplement person) {
        this.person = person;
    }

    public Concept getObservationConcept() {
        return observationConcept;
    }

    public void setObservationConcept(Concept observationConcept) {
        this.observationConcept = observationConcept;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getValueAsString() {
        return valueAsString;
    }

    public void setValueAsString(String valueAsString) {
        this.valueAsString = valueAsString;
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

    public Concept getType() {
        return type;
    }

    public void setType(Concept type) {
        this.type = type;
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

    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }

    public Concept getUnit() {
        return unit;
    }

    public void setUnit(Concept unit) {
        this.unit = unit;
    }

    public String getUnitSourceValue() {
        return unitSourceValue;
    }

    public void setUnitSourceValue(String unitSourceValue) {
        this.unitSourceValue = unitSourceValue;
    }

    @Override
    public IResourceEntity constructEntityFromResource(IResource resource) {
        ca.uhn.fhir.model.dstu2.resource.Observation observation = (ca.uhn.fhir.model.dstu2.resource.Observation) resource;

        if (observation.getId() != null && observation.getId().getIdPartAsLong() != null && observation.getId().getIdPart() != null) {
            // See if we already have this in the source field. If so,
            // then we want update not create
            final Observation origObservation = DAO.getInstance().loadEntityBySource(Observation.class, "Observation", "sourceValue", observation.getId().getIdPart());
            if (origObservation == null)
                this.setSourceValue(observation.getId().getIdPart());
            else
                this.setId(origObservation.getId());
        }

		/*
         * Set subject: currently supporting only type Person TODO create
		 * entity-complement to specify other types of subjects
		 */
        ResourceReferenceDt subjectReference = observation.getSubject();
        if ((subjectReference != null) && (observation.getSubject().getReference().getIdPartAsLong() != null)) {
            if ("Patient".equals(subjectReference.getReference().getResourceType())) {
                person = PersonComplement.searchAndUpdate(subjectReference);
                if (person == null) return null; // We must have a patient
            } else {
                // Group, Device, and Location are not supported in OMOP v5
                return null;
            }
        } else {
            // We must have subject reference.
            return null;
        }

        final CodingDt codeDt = observation.getCode().getCodingFirstRep();
        final Long id = ConceptDAO.getInstance().getConcept(codeDt);
        if (id != null) {
            observationConcept = new Concept(id);
        } else {
            logger.warn("Observation code not recognized.  Code: {}; System: {}", codeDt.getCode(), codeDt.getSystem());
        }

        if (observation.getEffective() != null)
        {
            if (observation.getEffective() instanceof DateTimeDt) {
                this.date = ((DateTimeDt) observation.getEffective()).getValue();
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                this.time = timeFormat.format(((DateTimeDt) observation.getEffective()).getValue());
            } else if (observation.getEffective() instanceof PeriodDt) {
                // TODO: we need to handle period. We can probably use
                // we can use range_low and range_high. These are only available in Measurement
            }
        }

		/* Set visit occurrence */
        Long visitOccurrenceId = observation.getEncounter().getReference().getIdPartAsLong();
        if (visitOccurrenceId != null) {
            this.visitOccurrence = new VisitOccurrence();
            this.visitOccurrence.setId(visitOccurrenceId);
        }

		/* Set the type of the observation */
        if (observation.getMethod() != null) {
            final CodingDt method = observation.getMethod().getCodingFirstRep();
            final Long mid = ConceptDAO.getInstance().getConcept(method);
            if (mid != null) type = new Concept(mid);
        }

		/* Set the value of the observation */
        IDatatype value = observation.getValue();
        if (value instanceof QuantityDt) {
            final QuantityDt qdt = (QuantityDt) value;
            String unitCode = qdt.getCode();
            if (unitCode == null) unitCode = qdt.getUnit();
            Long unitId = ConceptDAO.getInstance().getConcept(unitCode, qdt.getSystem());
            this.valueAsNumber = qdt.getValue().doubleValue();
            if (unitId != null) unit = new Concept(unitId);
        } else if (value instanceof CodeableConceptDt) {
            final CodeableConceptDt ccd = (CodeableConceptDt) value;
            final Long valueAsConceptId = ConceptDAO.getInstance().getConcept(ccd.getCodingFirstRep().getCode(), ccd.getCodingFirstRep().getSystem());
            if (valueAsConceptId != null) {
                valueAsConcept = new Concept(valueAsConceptId);
            }
        } else if (value instanceof StringDt) {
            String valueAsString = ((StringDt) value).getValueAsString();
            if (valueAsString != null) {
                this.valueAsString = valueAsString;
            }
        }

        // quick solution.
        this.sourceValue = "NA";

        return this;
    }

    @Override
    public FhirVersionEnum getFhirVersion() {
        return FhirVersionEnum.DSTU2;
    }

    @Override
    public IResource getRelatedResource() {
        ca.uhn.fhir.model.dstu2.resource.Observation observation = new ca.uhn.fhir.model.dstu2.resource.Observation();
        observation.setId(this.getIdDt());

        if (this.person != null) {
            ResourceReferenceDt personRef = new ResourceReferenceDt(this.person.getIdDt());
            personRef.setDisplay(this.person.getNameAsSingleString());
            observation.setSubject(personRef);
        }

        String systemUriString = null;
        String codeString = null;
        String displayString = null;

        if (observationConcept != null) {
            systemUriString = this.observationConcept.getVocabularyReference();
            codeString = this.observationConcept.getConceptCode();
            displayString = (observationConcept.getId() == 0L) ? getSourceValue() : observationConcept.getName();
        }

        {
            IDatatype value = null;
            if (this.valueAsNumber != null) {
                QuantityDt quantity = new QuantityDt(this.valueAsNumber.doubleValue());
                if (this.unit != null) {
                    // Unit is defined as a concept code in omop v4, then unit and code are the same in this case
                    quantity.setUnit(this.unit.getConceptCode());
                    quantity.setCode(this.unit.getConceptCode());
                    quantity.setSystem(this.unit.getVocabularyReference());
                }
                value = quantity;
            } else if (this.valueAsString != null) {
                value = new StringDt(this.valueAsString);
            } else if (this.valueAsConcept != null && this.valueAsConcept.getId() != 0L) {
                // vocabulary is a required attribute for concept, then it's expected to not be null
                CodeableConceptDt valueAsConcept = new CodeableConceptDt(this.valueAsConcept.getVocabularyReference(),
                        this.valueAsConcept.getConceptCode());
                value = valueAsConcept;
            }
            observation.setValue(value);
        }

        CodeableConceptDt code = new CodeableConceptDt(systemUriString, codeString);
        code.getCodingFirstRep().setDisplay(displayString);
        observation.setCode(code);

        observation.setStatus(STATUS);

        if (this.date != null) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = fmt.format(this.date);
            fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date myDate = null;
            try {
                if (this.time != null && this.time.isEmpty() == false) {
                    myDate = fmt.parse(dateString + " " + this.time);
                } else {
                    myDate = this.date;
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (myDate != null) {
                DateTimeDt appliesDate = new DateTimeDt(myDate);
                observation.setEffective(appliesDate);
            }
        }
        if (this.visitOccurrence != null)
            observation.getEncounter().setReference(new IdDt(VisitOccurrence.RES_TYPE, this.visitOccurrence.getId()));

        return observation;
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
            case ca.uhn.fhir.model.dstu2.resource.Observation.SP_SUBJECT:
                return "person";
            case ca.uhn.fhir.model.dstu2.resource.Observation.SP_PATIENT:
                return "person";
            case ca.uhn.fhir.model.dstu2.resource.Observation.SP_ENCOUNTER:
                return "visitOccurrence";
            case ca.uhn.fhir.model.dstu2.resource.Observation.SP_VALUE_QUANTITY:
                return "valueAsNumber";
            case ca.uhn.fhir.model.dstu2.resource.Observation.SP_VALUE_STRING:
                return "valueAsString";
            case ca.uhn.fhir.model.dstu2.resource.Observation.SP_VALUE_CONCEPT:
                return "valueAsConcept";
            default:
                break;
        }
        return theSearchParam;
    }

}
