package edu.gatech.i3l.fhir.dstu2.entities;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.primitive.BooleanDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import com.typesafe.config.Config;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import edu.gatech.i3l.fhir.jpa.entity.IResourceEntity;
import edu.gatech.i3l.omop.dao.ConceptDAO;
import edu.gatech.i3l.omop.dao.DeathDAO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
public class Person extends BaseResourceEntity {
    private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(Person.class);

    public static final String RES_TYPE = "Patient";
    public static final String US_CORE_RACE = "http://hl7.org/fhir/StructureDefinition/us-core-race";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id", updatable = false)
    @Access(AccessType.PROPERTY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "gender_concept_id", nullable = false, foreignKey = @ForeignKey(name = "fpk_person_gender_concept"))
    private Concept genderConcept;

    @Column(name = "year_of_birth", nullable = false)
    private Integer yearOfBirth;

    @Column(name = "month_of_birth")
    private Integer monthOfBirth;

    @Column(name = "day_of_birth")
    private Integer dayOfBirth;

    @Column(name = "time_of_birth")
    private String timeOfBirth;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "race_concept_id", foreignKey = @ForeignKey(name = "fpk_person_race_concept"))
    private Concept raceConcept;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "ethnicity_concept_id", foreignKey = @ForeignKey(name = "fpk_person_ethnicity_concept"))
    private Concept ethnicityConcept;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "location_id", foreignKey = @ForeignKey(name = "fpk_person_location"))
    private Location location;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", foreignKey = @ForeignKey(name = "fpk_person_provider"))
    private Provider provider;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "care_site_id", foreignKey = @ForeignKey(name = "fpk_person_care_site"))
    private CareSite careSite;

    @Column(name = "person_source_value")
    private String personSourceValue;

    @Column(name = "gender_source_value")
    private String genderSourceValue;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_source_concept_id", foreignKey = @ForeignKey(name = "fpk_person_gender_concept_s"))
    private Concept genderSourceConcept;

    @Column(name = "race_source_value")
    private String raceSourceValue;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "race_source_concept_id", foreignKey = @ForeignKey(name = "fpk_person_race_concept_s"))
    private Concept raceSourceConcept;

    @Column(name = "ethnicity_source_value")
    private String ethnicitySourceValue;

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "ethnicity_source_concept_id", foreignKey = @ForeignKey(name = "fpk_person_ethnicity_concept_s"))
    private Concept ethnicitySourceConcept;

//	@OneToMany(orphanRemoval=true, mappedBy="person")
//	private Set<ConditionOccurrence> conditions;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "person")
    @JoinColumn(name = "person_id")
    private Death death;

    public Person() {
        super();
        this.setYearOfBirth(0);
    }

    public Person(Long id, Concept genderConcept, Integer yearOfBirth, Integer monthOfBirth,
                  Integer dayOfBirth, String timeOfBirth, Concept raceConcept, Concept ethnicityConcept,
                  Location location, Provider provider, CareSite careSite, String personSourceValue,
                  String genderSourceValue, Concept genderSourceConcept, String raceSourceValue,
                  Concept raceSourceConcept, String ethnicitySourceValue, Concept ethnicitySourceConcept) {
        super();
        this.id = id;
        this.genderConcept = genderConcept;
        this.yearOfBirth = yearOfBirth;
        this.monthOfBirth = monthOfBirth;
        this.dayOfBirth = dayOfBirth;
        this.timeOfBirth = timeOfBirth;
        this.raceConcept = raceConcept;
        this.ethnicityConcept = ethnicityConcept;
        this.location = location;
        this.provider = provider;
        this.careSite = careSite;
        this.personSourceValue = personSourceValue;
        this.genderSourceValue = genderSourceValue;
        this.genderSourceConcept = genderSourceConcept;
        this.raceSourceValue = raceSourceValue;
        this.raceSourceConcept = raceSourceConcept;
        this.ethnicitySourceValue = ethnicitySourceValue;
        this.ethnicitySourceConcept = ethnicitySourceConcept;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Concept getGenderConcept() {
        return genderConcept;
    }

    public void setGenderConcept(Concept genderConcept) {
        this.genderConcept = genderConcept;
    }

    public Integer getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(Integer yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public Integer getMonthOfBirth() {
        return monthOfBirth;
    }

    public void setMonthOfBirth(Integer monthOfBirth) {
        this.monthOfBirth = monthOfBirth;
    }

    public Integer getDayOfBirth() {
        return dayOfBirth;
    }

    public void setDayOfBirth(Integer dayOfBirth) {
        this.dayOfBirth = dayOfBirth;
    }

    public String getTimeOfBirth() {
        return timeOfBirth;
    }

    public void setTimeOfBirth(String timeOfBirth) {
        this.timeOfBirth = timeOfBirth;
    }

    public Concept getRaceConcept() {
        return raceConcept;
    }

    public void setRaceConcept(Concept raceConcept) {
        this.raceConcept = raceConcept;
    }

    public Concept getEthnicityConcept() {
        return ethnicityConcept;
    }

    public void setEthnicityConcept(Concept ethnicityConcept) {
        this.ethnicityConcept = ethnicityConcept;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public CareSite getCareSite() {
        return careSite;
    }

    public void setCareSite(CareSite careSite) {
        this.careSite = careSite;
    }

    public String getPersonSourceValue() {
        return personSourceValue;
    }

    public void setPersonSourceValue(String personSourceValue) {
        this.personSourceValue = personSourceValue;
    }

    public String getGenderSourceValue() {
        return genderSourceValue;
    }

    public void setGenderSourceValue(String genderSourceValue) {
        this.genderSourceValue = genderSourceValue;
    }

    public Concept getGenderSourceConcept() {
        return genderSourceConcept;
    }

    public void setGenderSourceConcept(Concept genderSourceConcept) {
        this.genderSourceConcept = genderSourceConcept;
    }

    public String getRaceSourceValue() {
        return raceSourceValue;
    }

    public void setRaceSourceValue(String raceSourceValue) {
        this.raceSourceValue = raceSourceValue;
    }

    public Concept getRaceSourceConcept() {
        return raceSourceConcept;
    }

    public void setRaceSourceConcept(Concept raceSourceConcept) {
        this.raceSourceConcept = raceSourceConcept;
    }

    public String getEthnicitySourceValue() {
        return ethnicitySourceValue;
    }

    public void setEthnicitySourceValue(String ethnicitySourceValue) {
        this.ethnicitySourceValue = ethnicitySourceValue;
    }

    public Concept getEthnicitySourceConcept() {
        return ethnicitySourceConcept;
    }

    public void setEthnicitySourceConcept(Concept ethnicitySourceConcept) {
        this.ethnicitySourceConcept = ethnicitySourceConcept;
    }

	public Death getDeath() { return death; }
	public void setDeath(Death death) { this.death = death; }

    @Override
    public Patient getRelatedResource() {
        Patient patient = new Patient();
        patient.setId(this.getIdDt());

        Calendar calendar = Calendar.getInstance();
        int yob, mob, dob;
        if (this.yearOfBirth != null) yob = this.yearOfBirth;
        else yob = 1;
        if (this.monthOfBirth != null) mob = this.monthOfBirth;
        else mob = 1;
        if (this.dayOfBirth != null) dob = this.dayOfBirth;
        else dob = 1;
        calendar.set(yob, mob - 1, dob);
        patient.setBirthDate(new DateDt(calendar.getTime()));

        if (this.location != null && this.location.getId() != 0L) {
            patient.addAddress()
                    .setUse(AddressUseEnum.HOME)
                    .addLine(this.location.getAddress1())
                    .addLine(this.location.getAddress2())//WARNING check if mapping for lines are correct
                    .setCity(this.location.getCity())
                    .setPostalCode(this.location.getZipCode())
                    .setState(this.location.getState());
        }

        if (this.genderConcept != null) {
            AdministrativeGenderEnum admGender = null;//TODO check if DSTU2 uses values coherent with this enum
            String gName = this.genderConcept.getName();
            AdministrativeGenderEnum[] values = AdministrativeGenderEnum.values();
            for (int i = 0; i < values.length; i++) {
                if (gName.equalsIgnoreCase(values[i].getCode())) {
                    admGender = values[i];
                    break;
                }
            }
            patient.setGender(admGender);
        }

        if (this.provider != null && this.provider.getId() != 0L) {
            ResourceReferenceDt practitionerResourceRef = new ResourceReferenceDt(this.provider.getIdDt());
            practitionerResourceRef.setDisplay(this.provider.getProviderName());
            List<ResourceReferenceDt> pracResourceRefs = new ArrayList<ResourceReferenceDt>();
            pracResourceRefs.add(practitionerResourceRef);
            patient.setCareProvider(pracResourceRefs);
        }


        addDeath(patient);
        addRace(patient);

        return patient;
    }

    @Override
    public String getResourceType() {
        return RES_TYPE;
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
    public IResourceEntity constructEntityFromResource(IResource resource) {
        if (!( resource instanceof Patient)) return this;

        Patient patient = (Patient) resource;

        if (patient.getBirthDate() != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(patient.getBirthDate());
            this.yearOfBirth = c.get(Calendar.YEAR);
            this.monthOfBirth = c.get(Calendar.MONTH) + 1;
            this.dayOfBirth = c.get(Calendar.DAY_OF_MONTH);
        }

        if (patient.getGender() != null) {
            final ConceptDAO dao = ConceptDAO.getInstance();
            final Config config = dao.config.getConfig("concept.gender");
            if (config.hasPath(patient.getGender())) {
                final Config gender = config.getConfig(patient.getGender());
                genderConcept = new Concept(dao.getConcept(gender.getString("code"), gender.getString("url")));
            }
        }

        List<AddressDt> addresses = patient.getAddress();
        if (addresses != null && addresses.size() > 0) {
            AddressDt address = addresses.get(0);

            Location retLocation = Location.searchAndUpdate(address, this.location);
            if (retLocation != null) {
                this.setLocation(retLocation);
            }
        }

        List<ResourceReferenceDt> providers = patient.getCareProvider();
        if (providers.size() > 0) {
            // We can handle only one provider.
            this.setProvider(Provider.searchAndUpdate(providers.get(0)));
        }

        processDeath(patient);
        processRace(patient);

        return this;
    }

    @Override
    public String translateSearchParam(String link) {
        switch (link) {
            case Patient.SP_CAREPROVIDER:
                return "provider";
            default:
                break;
        }

        return link;
    }

    private void processDeath(final Patient patient) {
        final IDatatype deceased = patient.getDeceased();
        if (deceased != null) {
            if (deceased instanceof BooleanDt) {
                final BooleanDt value = (BooleanDt) patient.getDeceased();
                if (value.getValue()) {
                    death = DeathDAO.getInstance().getById(id);
                    if (death == null) death = new Death(this);
                } else {
                    death = null;
                    DeathDAO.getInstance().remove(id);
                }
            }
            else if (deceased instanceof DateTimeDt) {
                final DateTimeDt value = (DateTimeDt) deceased;
                death = DeathDAO.getInstance().getById(id);
                if (death == null) death = new Death(this);
                death.setDeathDate(value.getValue());
            }
        } else death = null;
    }

    private void addDeath(final Patient patient) {
        if (death != null) {
            if (death.getDeathDate() != null) {
                patient.setDeceased(new DateTimeDt(death.getDeathDate()));
            } else {
                patient.setDeceased(new BooleanDt(true));
            }
        }
        else patient.setDeceased(new BooleanDt(false));
    }

    private void processRace(final Patient patient) {
        for (final ExtensionDt extension : patient.getAllUndeclaredExtensions()) {
            switch (extension.getUrl()) {
                case US_CORE_RACE:
                    if (extension.getValue() instanceof CodeableConceptDt) {
                        final CodeableConceptDt cdt = (CodeableConceptDt) extension.getValue();
                        final CodingDt cd = cdt.getCodingFirstRep();
                        final Long cid = ConceptDAO.getInstance().getConcept(cd);
                        if (cid != null) raceConcept = new Concept(cid);
                        else {
                            ourLog.warn("Unsupported race extension system: {} with value: {} and text: {}",
                                    cd.getSystem(), cd.getCode(), cd.getDisplay());
                            raceConcept = new Concept(ConceptDAO.getInstance().getDefaultRace());
                        }
                    }
                    break;
            }
        }
    }

    private void addRace(final Patient patient) {
        if (raceConcept != null) {
            final CodeableConceptDt cdt = new CodeableConceptDt();
            cdt.addCoding(new CodingDt(raceConcept.getVocabulary(),raceConcept.getConceptCode()));
            final ExtensionDt edt = new ExtensionDt();
            edt.setUrl(US_CORE_RACE);
            edt.setValue(cdt);
            patient.addUndeclaredExtension(edt);
        }
    }
}
