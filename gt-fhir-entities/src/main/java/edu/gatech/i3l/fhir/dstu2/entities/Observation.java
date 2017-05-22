package edu.gatech.i3l.fhir.dstu2.entities;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.Observation.Component;
import ca.uhn.fhir.model.dstu2.valueset.ObservationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import edu.gatech.i3l.fhir.jpa.entity.IResourceEntity;
import edu.gatech.i3l.omop.enums.Omop4ConceptsFixedIds;
import edu.gatech.i3l.omop.mapping.OmopConceptMapping;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Observation extends BaseResourceEntity {

    public static final Long SYSTOLIC_CONCEPT_ID = 3004249L;
    public static final Long DIASTOLIC_CONCEPT_ID = 3012888L;
    private static final String RES_TYPE = "Observation";
    private static final ObservationStatusEnum STATUS = ObservationStatusEnum.FINAL;

    private Long id;
    private PersonComplement person;
    private Concept observationConcept;
    private Date date;
    private String time;
    private String valueAsString;
    private BigDecimal valueAsNumber;
    private BigDecimal rangeLow;
    private BigDecimal rangeHigh;
    private Concept valueAsConcept;
    private Concept type;
    private Provider provider;
    private VisitOccurrence visitOccurrence;
    private String sourceValue;
    private String valueSourceValue;
    private Concept unit;
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

    public BigDecimal getRangeLow() {
        return rangeLow;
    }

    public void setRangeLow(BigDecimal rangeLow) {
        this.rangeLow = rangeLow;
    }

    public BigDecimal getRangeHigh() {
        return rangeHigh;
    }

    public void setRangeHigh(BigDecimal rangeHigh) {
        this.rangeHigh = rangeHigh;
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

    public BigDecimal getValueAsNumber() {
        return valueAsNumber;
    }

    public void setValueAsNumber(BigDecimal valueAsNumber) {
        this.valueAsNumber = valueAsNumber;
    }

    public Concept getValueAsConcept() {
        return valueAsConcept;
    }

    public void setValueAsConcept(Concept valueAsConcept) {
        this.valueAsConcept = valueAsConcept;
    }

//	public Concept getRelevantCondition() {
//		return relevantCondition;
//	}
//
//	public void setRelevantCondition(Concept relevantCondition) {
//		this.relevantCondition = relevantCondition;
//	}

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

    public String getValueSourceValue() {
        return valueSourceValue;
    }

    public void setValueSourceValue(String valueSourceValue) {
        this.valueSourceValue = valueSourceValue;
    }

    @Override
    public IResourceEntity constructEntityFromResource(IResource resource) {
        System.out.println("Trying to write to Observation View Table");
        // TODO: This is view, which is read-only. We need to come up with a way to write
        // to either measurement or observation tables in OMOP. We may write them manually
        // and just return null for this. But then, response will not be correct. Revisit this.
        ca.uhn.fhir.model.dstu2.resource.Observation observation = (ca.uhn.fhir.model.dstu2.resource.Observation) resource;
        OmopConceptMapping ocm = OmopConceptMapping.getInstance();

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

		/*
         * Set subject: currently supporting only type Person TODO create
		 * entity-complement to specify other types of subjects
		 */
        IdDt reference = observation.getSubject().getReference();
        if (reference.getIdPartAsLong() != null) {
            if ("Patient".equals(reference.getResourceType())) {
                this.person = new PersonComplement();
                this.person.setId(reference.getIdPartAsLong());
            } else if ("Group".equals(reference.getResourceType())) {
                //
            } else if ("Device".equals(reference.getResourceType())) {
                //
            } else if ("Location".equals(reference.getResourceType())) {
                //
            }
        }

		/* Set visit occurrence */
        Long visitOccurrenceId = observation.getEncounter().getReference().getIdPartAsLong();
        if (visitOccurrenceId != null) {
            this.visitOccurrence = new VisitOccurrence();
            this.visitOccurrence.setId(visitOccurrenceId);
        }

        Long observationConceptId = ocm.get(observation.getCode().getCodingFirstRep().getCode(),
                OmopConceptMapping.LOINC_CODE);
        if (observationConceptId != null) {
            this.observationConcept = new Concept();
            this.observationConcept.setId(observationConceptId);
        }

		/* Set the type of the observation */
        this.type = new Concept();
        if (observation.getMethod().getCodingFirstRep() != null) {
            this.type.setId(Omop4ConceptsFixedIds.OBSERVATION_FROM_LAB_NUMERIC_RESULT.getConceptId()); // assuming
            // all
            // results
            // on
            // this
            // table
            // are
            // quantitative:
            // http://hl7.org/fhir/2015May/valueset-observation-methods.html
        } else {
            this.type.setId(Omop4ConceptsFixedIds.OBSERVATION_FROM_EHR.getConceptId());
        }

		/* Set the value of the observation */
        IDatatype value = observation.getValue();
        if (value instanceof QuantityDt) {
            Long unitId = ocm.get(((QuantityDt) value).getUnit(), OmopConceptMapping.UCUM_CODE,
                    OmopConceptMapping.UCUM_CODE_STANDARD, OmopConceptMapping.UCUM_CODE_CUSTOM);
            this.valueAsNumber = ((QuantityDt) value).getValue();
            if (unitId != null) {
                this.unit = new Concept();
                this.unit.setId(unitId);
            }
            this.rangeHigh = observation.getReferenceRangeFirstRep().getHigh().getValue();
            this.rangeLow = observation.getReferenceRangeFirstRep().getLow().getValue();
        } else if (value instanceof CodeableConceptDt) {
            Long valueAsConceptId = ocm.get(((CodeableConceptDt) value).getCodingFirstRep().getCode(),
                    OmopConceptMapping.CLINICAL_FINDING);
            if (valueAsConceptId != null) {
                this.valueAsConcept = new Concept();
                this.valueAsConcept.setId(valueAsConceptId);
            }
        } else {
            this.valueAsString = ((StringDt) value).getValue();
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

        String systemUriString = null;
        String codeString = null;
        String displayString = null;

        if (observationConcept != null) {
            systemUriString = this.observationConcept.getVocabulary().getSystemUri();
            codeString = this.observationConcept.getConceptCode();
            displayString = (observationConcept.getId() == 0L) ? getSourceValue() : observationConcept.getName();
        }

        // OMOP database maintains Systolic and Diastolic Blood Pressures separately.
        // FHIR however keeps them together. Observation DAO filters out Diastolic values.
        // Here, when we are reading systolic, we search for matching diastolic and put them
        // together. The Observation ID will be systolic's OMOP ID.
        // public static final Long SYSTOLIC_CONCEPT_ID = new Long(3004249);
        // public static final Long DIASTOLIC_CONCEPT_ID = new Long(3012888);
        if (observationConcept != null && SYSTOLIC_CONCEPT_ID.equals(observationConcept.getId())) {
            // Set coding for systolic and diastolic observation
            systemUriString = "http://loinc.org";
            codeString = "55284-4";
            displayString = "Blood pressure systolic & diastolic";

            List<Component> components = new ArrayList<Component>();
            // First we add systolic component.
            Component comp = new Component();
            CodeableConceptDt componentCode = new CodeableConceptDt(this.observationConcept.getVocabulary().getSystemUri(),
                    this.observationConcept.getConceptCode());
            componentCode.getCodingFirstRep().setDisplay(this.observationConcept.getName());
            comp.setCode(componentCode);

            IDatatype compValue = null;
            if (this.valueAsNumber != null) {
                QuantityDt quantity = new QuantityDt(this.valueAsNumber.doubleValue());
                // Unit is defined as a concept code in omop v4, then unit and code are the same in this case
                if (this.unit != null) {
                    quantity.setUnit(this.unit.getConceptCode());
                    quantity.setCode(this.unit.getConceptCode());
                    quantity.setSystem(this.unit.getVocabulary().getSystemUri());
                }
                compValue = quantity;
                comp.setValue(compValue);
                components.add(comp);
            }

            if (components.size() > 0) {
                observation.setComponent(components);
            }
        } else {
            IDatatype value = null;
            if (this.valueAsNumber != null) {
                QuantityDt quantity = new QuantityDt(this.valueAsNumber.doubleValue());
                if (this.unit != null) {
                    // Unit is defined as a concept code in omop v4, then unit and code are the same in this case
                    quantity.setUnit(this.unit.getConceptCode());
                    quantity.setCode(this.unit.getConceptCode());
                    quantity.setSystem(this.unit.getVocabulary().getSystemUri());
                }
                value = quantity;
            } else if (this.valueAsString != null) {
                value = new StringDt(this.valueAsString);
            } else if (this.valueAsConcept != null && this.valueAsConcept.getId() != 0L) {
                // vocabulary is a required attribute for concept, then it's expected to not be null
                CodeableConceptDt valueAsConcept = new CodeableConceptDt(this.valueAsConcept.getVocabulary().getSystemUri(),
                        this.valueAsConcept.getConceptCode());
                value = valueAsConcept;
            } else {
                value = new StringDt(this.getValueSourceValue());
            }
            observation.setValue(value);
        }

        if (this.rangeLow != null)
            observation.getReferenceRangeFirstRep().setLow(new SimpleQuantityDt(this.rangeLow.doubleValue()));
        if (this.rangeHigh != null)
            observation.getReferenceRangeFirstRep().setHigh(new SimpleQuantityDt(this.rangeHigh.doubleValue()));

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
        if (this.person != null) {
            ResourceReferenceDt personRef = new ResourceReferenceDt(this.person.getIdDt());
            personRef.setDisplay(this.person.getNameAsSingleString());
            observation.setSubject(personRef);
        }
        if (this.visitOccurrence != null)
            observation.getEncounter().setReference(new IdDt(VisitOccurrence.RES_TYPE, this.visitOccurrence.getId()));

        if (type != null) {
            if (type.getId() == 44818701L || type.getId() == 38000280L || type.getId() == 38000281L) {
                // This is From physical examination.
                CodeableConceptDt typeConcept = new CodeableConceptDt();
                List<CodingDt> typeCodings = new ArrayList<CodingDt>();
                CodingDt typeCoding = new CodingDt("http://hl7.org/fhir/observation-category", "exam");
                typeCodings.add(typeCoding);
                typeConcept.setCoding(typeCodings);
                observation.setCategory(typeConcept);
            } else if (type.getId() == 45905771L) {
                CodeableConceptDt typeConcept = new CodeableConceptDt();
                // This is Lab result
                List<CodingDt> typeCodings = new ArrayList<CodingDt>();
                CodingDt typeCoding = new CodingDt("http://hl7.org/fhir/observation-category", "survey");
                typeCodings.add(typeCoding);
                typeConcept.setCoding(typeCodings);
                observation.setCategory(typeConcept);
            } else if (type.getId() == 38000277L || type.getId() == 38000278L || type.getId() == 44818702L) {
                CodeableConceptDt typeConcept = new CodeableConceptDt();
                // This is Lab result
                List<CodingDt> typeCodings = new ArrayList<CodingDt>();
                CodingDt typeCoding = new CodingDt("http://hl7.org/fhir/observation-category", "laboratory");
                typeCodings.add(typeCoding);
                typeConcept.setCoding(typeCodings);
                observation.setCategory(typeConcept);
            }
        }

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
        System.out.println("Observation Search:" + theSearchParam);
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
