/**
 *
 */
package edu.gatech.i3l.fhir.dstu2.entities;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.valueset.ConditionCategoryCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.ConditionVerificationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import com.typesafe.config.Config;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import edu.gatech.i3l.fhir.jpa.entity.IResourceEntity;
import edu.gatech.i3l.omop.dao.ConceptDAO;
import edu.gatech.i3l.omop.dao.DAO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

import static java.lang.String.format;

/**
 * @author Myung Choi
 */
@Entity
@Table(name = "condition_occurrence")
public class ConditionOccurrence extends BaseResourceEntity {

    public static final String RESOURCE_TYPE = "Condition";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "condition_occurrence_id", updatable = false)
    @Access(AccessType.PROPERTY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "person_id", nullable = false, foreignKey = @ForeignKey(name = "fpk_condition_person"))
    @NotNull
    private PersonComplement person;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "condition_concept_id", nullable = false, foreignKey = @ForeignKey(name = "fpk_condition_concept"))
    @NotNull
    private Concept conditionConcept;

    @Column(name = "condition_start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "condition_end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "condition_type_concept_id", nullable = false, foreignKey = @ForeignKey(name = "fpk_condition_type_concept"))
    @NotNull
    private Concept conditionTypeConcept;

    @Column(name = "stop_reason")
    private String stopReason;

    /**
     * @omop
     * @fhir Asserter:
     * person who asserts this condition (Practitioner or Patient)
     * @fhirVersion 0.4.0
     * @omopVersion 4.0
     */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "provider_id", foreignKey = @ForeignKey(name = "fpk_condition_provider"))
    private Provider provider;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "visit_occurrence_id", foreignKey = @ForeignKey(name = "fpk_condition_visit"))
    private VisitOccurrence encounter;

    @Column(name = "condition_source_value")
    private String sourceValue;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "condition_source_concept_id", foreignKey = @ForeignKey(name = "fpk_condition_concept_s"))
    private Concept sourceConcept;

    public ConditionOccurrence() {
        super();
    }

    public ConditionOccurrence(Long id, PersonComplement person, Concept conditionConcept, Date startDate, Date endDate,
                               Concept conditionTypeConcept, String stopReason, Provider provider, VisitOccurrence encounter,
                               String sourceValue, Concept sourceConcept) {
        super();

        this.id = id;
        this.person = person;
        this.conditionConcept = conditionConcept;
        this.startDate = startDate;
        this.endDate = endDate;
        this.conditionTypeConcept = conditionTypeConcept;
        this.stopReason = stopReason;
        this.provider = provider;
        this.encounter = encounter;
        this.sourceValue = sourceValue;
        this.sourceConcept = sourceConcept;
    }


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getResourceType() {
        return RESOURCE_TYPE;
    }

    public PersonComplement getPerson() {
        return person;
    }

    public void setPerson(PersonComplement person) {
        this.person = person;
    }

    public Concept getConditionConcept() {
        return conditionConcept;
    }

    public void setConditionConcept(Concept conditionConcept) {
        this.conditionConcept = conditionConcept;
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

    public Concept getConditionTypeConcept() {
        return conditionTypeConcept;
    }

    public void setConditionTypeConcept(Concept conditionTypeConcept) {
        this.conditionTypeConcept = conditionTypeConcept;
    }

    public String getStopReason() {
        return stopReason;
    }

    public void setStopReason(String stopReason) {
        this.stopReason = stopReason;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public VisitOccurrence getEncounter() {
        return encounter;
    }

    public void setEncounter(VisitOccurrence encounter) {
        this.encounter = encounter;
    }

    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }

    public Concept getSourceConcept() {
        return sourceConcept;
    }

    public void setSourceConcept(Concept sourceConcept) {
        this.sourceConcept = sourceConcept;
    }

    @Override
    public IResourceEntity constructEntityFromResource(IResource resource) {
        if (resource instanceof Condition) {
            Condition condition = (Condition) resource;

			/* Set patient */
            ResourceReferenceDt patientResource = condition.getPatient();
            if (patientResource == null) return null; // We have to have a patient

            PersonComplement person = PersonComplement.searchAndUpdate(patientResource);
            this.setPerson(person);
            if (person == null) return null; // We must have a patient


            // We are writing to the database. Keep the source so we know where it is coming from
            if (condition.getId() != null) {
                // See if we already have this in the source field. If so,
                // then we want update not create
                final ConditionOccurrence origCondition = DAO.getInstance().loadEntityBySource(ConditionOccurrence.class, "ConditionOccurrence", "sourceValue", condition.getId().getIdPart());
                if (origCondition == null)
                    this.sourceValue = condition.getId().getIdPart();
                else
                    this.setId(origCondition.getId());
            }

            if (condition.getCode() != null) {
                final CodingDt cdt = condition.getCode().getCodingFirstRep();
                final Long cid = ConceptDAO.getInstance().getConcept(cdt);
                if (cid != null) conditionConcept = new Concept(cid);
            }

            IDatatype onSetDate = condition.getOnset();
            if (onSetDate instanceof DateTimeDt) {
                DateTimeDt dateTimeDt = (DateTimeDt) onSetDate;
                this.startDate = dateTimeDt.getValue();
            } else if (onSetDate instanceof PeriodDt) {
                PeriodDt periodDt = (PeriodDt) onSetDate;
                this.startDate = periodDt.getStart();
                this.endDate = periodDt.getEnd();
            }

            ResourceReferenceDt encounterResource = condition.getEncounter();
            if (encounterResource != null) {
                Long encounterReference = encounterResource.getReference().getIdPartAsLong();
                if (encounterReference != null) {
                    VisitOccurrence visitOccurrence = VisitOccurrence
                            .searchAndUpdate(encounterReference, startDate, endDate, this.person);
                    if (visitOccurrence != null) {
                        this.setEncounter(visitOccurrence);
                    }
                }
            }

            if (condition.getCategory() != null) {
                final BoundCodeableConceptDt<ConditionCategoryCodesEnum> condCategory = condition.getCategory();
                final CodingDt condCatCoding = condCategory.getCodingFirstRep();
                final Config config = ConceptDAO.getInstance().config.getConfig("concept.condition.category");

                if (condCatCoding != null) {
                    if (config.hasPath(condCatCoding.getCode())) {
                        final Config conf = config.getConfig(condCatCoding.getCode());
                        final Long id = ConceptDAO.getInstance().getConcept(conf.getString("code"), conf.getString("url"));
                        if (id != null) conditionTypeConcept = new Concept(id);
                    } else {
                        final Long id = ConceptDAO.getInstance().getConcept(condCatCoding);
                        if (id != null) conditionTypeConcept = new Concept(id);
                    }
                } else {
                    final Config conf = config.getConfig("complaint");
                    final Long id = ConceptDAO.getInstance().getConcept(conf.getString("code"), conf.getString("url"));
                    if (id != null) conditionTypeConcept = new Concept(id);
                }
            }

            // this.stopReason = stopReason; NOTE: no FHIR parameter for
            // stopReason.

            IdDt asserterReference = condition.getAsserter().getReference();
            ResourceReferenceDt asserterResRef = condition.getAsserter();
            if (asserterResRef != null && asserterReference.getIdPartAsLong() != null && asserterReference.getResourceType() != null
                    && asserterReference.getResourceType().equalsIgnoreCase(Provider.RESOURCE_TYPE)) {
                Long providerId = asserterReference.getIdPartAsLong();
                if (providerId != null) {
                    Provider provider = DAO.getInstance().loadEntityById(Provider.class, providerId);
                    if (provider != null) {
                        this.setProvider(provider);
                    } else {
                        // See if we have received this earlier.
                        provider = DAO.getInstance().loadEntityBySource(Provider.class, "Provider", "providerSourceValue", providerId.toString());
                        if (provider == null) {
                            this.provider = new Provider();
                            this.provider.setProviderName(asserterResRef.getDisplay().getValueAsString());
                            this.provider.setProviderSourceValue(providerId.toString());
                        } else {
                            this.setProvider(provider);
                        }
                    }
                }
            }

            //this.sourceValue = "FHIRCREATE";
        }

        return this;
    }

    @Override
    public Condition getRelatedResource() {
        Condition condition = new Condition();

        // Populate condition parameters.
        // Refer to 4.3.3 at http://hl7-fhir.github.io/condition.html

        condition.setId(this.getIdDt());

        // Set patient reference to Patient (note: in dstu1, this was subject.)
        ResourceReferenceDt patientReference = new ResourceReferenceDt(new IdDt(Person.RES_TYPE, this.person.getId()));
        patientReference.setDisplay(this.person.getNameAsSingleString());
        condition.setPatient(patientReference);

        // Set encounter if exists.
        if (encounter != null && encounter.getId() > 0) {
            // FIXME: encounter resource not yet implemented.
            // we just create this reference resource manually. When encounter
            // is implemented, we
            // will get it from visit_occurrence class.
            ResourceReferenceDt encounterReference = new ResourceReferenceDt(new IdDt(VisitOccurrence.RES_TYPE, this.encounter.getId()));
            condition.setEncounter(encounterReference);
        }

        // Set asserter if exists
        // This can be either Patient or Practitioner.
        if (provider != null && provider.getId() > 0) {
            ResourceReferenceDt practitionerReference = new ResourceReferenceDt(new IdDt(Provider.RESOURCE_TYPE, provider.getId()));
            practitionerReference.setDisplay(this.provider.getProviderName());
            condition.setAsserter(practitionerReference);
        }

        // Set Code
        // System.out.println("ConceptID:"+this.getConditionConcept().getId().toString());
        // System.out.println("ConceptName:"+this.getConditionConcept().getName());
        //
        // Vocabulary myVoc = this.getConditionConcept().getVocabulary();
        //
        // System.out.println("VocabularyID:"+myVoc.getId());
        // System.out.println("VocabularyName:"+myVoc.getName());

        // First check if ICD codes are available.
        String theSystem;
        String theCode;
        String theDisplay = "";
//		if (this.sourceValue.startsWith("icd-9-cm:") == true) {
//			theSystem = "http://hl7.org/fhir/sid/icd-9-cm";
//			theCode = this.sourceValue.substring(9);
//		} else {
        theSystem = conditionConcept.getVocabularyReference();
        theCode = conditionConcept.getConceptCode();
        theDisplay = conditionConcept.getName();
//		}

        CodeableConceptDt conditionCodeConcept = new CodeableConceptDt();
        if (theSystem != "") {
            // Create coding here. We have one coding in this condition as OMOP
            // allows one coding concept per condition.
            // In the future, if we want to allow multiple coding concepts here,
            // we need to do it here.
            CodingDt coding = new CodingDt(theSystem, theCode);
            coding.setDisplay(theDisplay);
            conditionCodeConcept.addCoding(coding);
        }

        // FHIR does not require the coding. If our System URI is not mappable
        // from
        // OMOP database, then coding would be empty. Set Text here. Even text
        // is not
        // required in FHIR. But, then no reason to have this condition, I
        // think...
        final String theText = format( "%s, %s, %s", conditionConcept.getName(),
                conditionConcept.getVocabularyReference(), conditionConcept.getConceptCode());

        conditionCodeConcept.setText(theText);
        condition.setCode(conditionCodeConcept);

        // Set onset[x]
        // We may have only one date if this condition did not end. If ended, we
        // have
        // a period. First, check if endDate is available.
        if (startDate != null) {
            DateTimeDt startDateDt = new DateTimeDt(startDate);
            if (endDate == null) {
                // Date
                condition.setOnset(startDateDt);
            } else {
                // Period
                DateTimeDt endDateDt = new DateTimeDt(endDate);
                PeriodDt periodDt = new PeriodDt();
                periodDt.setStart(startDateDt);
                periodDt.setEnd(endDateDt);
                condition.setOnset(periodDt);
            }
        }

        addCategory(condition);

        // VerficationStutus
        condition.setVerificationStatus(ConditionVerificationStatusEnum.CONFIRMED);

        return condition;
    }


    @Override
    public FhirVersionEnum getFhirVersion() {
        return FhirVersionEnum.DSTU2;
    }

    @Override
    public InstantDt getUpdated() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String translateSearchParam(String link) {
        switch (link) {
            case Condition.SP_CODE:
                return "conditionConcept.conceptCode";
            case Condition.SP_ONSET:
                return "startDate";
            case Condition.SP_ENCOUNTER:
                return "encounter";
            case Condition.SP_PATIENT:
                return "person";
//		case Condition.SP_SUBJECT:
//			return "person";
            default:
                break;
        }
        return link;
    }

    private void addCategory(final Condition condition) {
        if (conditionConcept == null) return;

        final ConceptDAO dao = ConceptDAO.getInstance();
        final String url = dao.getSystemUri(conditionConcept.getVocabulary());
        final Config conf = dao.config.getConfig("concept.condition.category");

        Config item = conf.getConfig("complaint");
        if (url.equals(item.getString("url")) && conditionConcept.getConceptCode().equals(item.getString("code"))) {
            condition.setCategory(ConditionCategoryCodesEnum.COMPLAINT);
            return;
        }

        item = conf.getConfig("symptom");
        if (url.equals(item.getString("url")) && conditionConcept.getConceptCode().equals(item.getString("code"))) {
            condition.setCategory(ConditionCategoryCodesEnum.SYMPTOM);
            return;
        }

        item = conf.getConfig("finding");
        if (url.equals(item.getString("url")) && conditionConcept.getConceptCode().equals(item.getString("code"))) {
            condition.setCategory(ConditionCategoryCodesEnum.FINDING);
            return;
        }

        item = conf.getConfig("diagnosis");
        if (url.equals(item.getString("url")) && conditionConcept.getConceptCode().equals(item.getString("code"))) {
            condition.setCategory(ConditionCategoryCodesEnum.DIAGNOSIS);
            return;
        }
    }
}