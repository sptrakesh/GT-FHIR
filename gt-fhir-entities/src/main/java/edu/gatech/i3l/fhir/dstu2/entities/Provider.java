package edu.gatech.i3l.fhir.dstu2.entities;

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.resource.Practitioner.PractitionerRole;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;
import edu.gatech.i3l.fhir.jpa.dao.BaseFhirDao;
import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import edu.gatech.i3l.fhir.jpa.entity.IResourceEntity;
import edu.gatech.i3l.omop.dao.DAO;
import edu.gatech.i3l.omop.dao.ProviderDAO;
import edu.gatech.i3l.omop.mapping.OmopConceptMapping;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;

@Entity
@Table(name = "provider")
@Inheritance(strategy = InheritanceType.JOINED)
public class Provider extends BaseResourceEntity {

    public static final String RESOURCE_TYPE = "Practitioner";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "provider_id", updatable = false)
    @Access(AccessType.PROPERTY)
    private Long id;

    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "npi")
    private String npi;

    @Column(name = "dea")
    private String dea;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "specialty_concept_id")
    private Concept specialtyConcept;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "care_site_id")
    private CareSite careSite;

    @Column(name = "year_of_birth")
    private Integer yearOfBirth;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "gender_concept_id")
    private Concept genderConcept;

    @Column(name = "provider_source_value")
    private String providerSourceValue;

    @Column(name = "specialty_source_value")
    private String specialtySourceValue;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "specialty_source_concept_id")
    private Concept specialtySourceConcept;

    @Column(name = "gender_source_value")
    private String genderSourceValue;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "gender_source_concept_id")
    private Concept genderSourceConcept;

    public Provider() {
        super();
    }

    public Provider(Long id, String providerName, String npi, String dea, Concept specialtyConcept,
                    CareSite careSite, Integer yearOfBirth, Concept genderConcept, String providerSourceValue,
                    String specialtySourceValue, Concept specialtySourceConcept, String genderSourceValue,
                    Concept genderSourceConcept) {
        this.id = id;
        this.providerName = providerName;
        this.npi = npi;
        this.dea = dea;
        this.specialtyConcept = specialtyConcept;
        this.careSite = careSite;
        this.yearOfBirth = yearOfBirth;
        this.genderConcept = genderConcept;
        this.providerSourceValue = providerSourceValue;
        this.specialtySourceValue = specialtySourceValue;
        this.specialtySourceConcept = specialtySourceConcept;
        this.genderSourceValue = genderSourceValue;
        this.genderSourceConcept = genderSourceConcept;
    }

    public static Provider searchAndUpdate(ResourceReferenceDt resourceRef) {
        if (resourceRef == null) return null;

        // See if this exists.
        Provider provider = DAO.getInstance().loadEntityById(Provider.class, resourceRef.getReference().getIdPartAsLong());
        if (provider != null) {
            return provider;
        } else {
            // Check source column to see if we have received this before.
            provider = (Provider) OmopConceptMapping.getInstance()
                    .loadEntityBySource(Provider.class, "Provider", "providerSourceValue", resourceRef.getReference().getIdPart());
            if (provider != null) {
                return provider;
            } else {
                provider = new Provider();
                provider.setProviderSourceValue(resourceRef.getReference().getIdPart());
                if (resourceRef.getDisplay() != null)
                    provider.setProviderName(resourceRef.getDisplay().toString());
                return provider;
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    public String getDea() {
        return dea;
    }

    public void setDea(String dea) {
        this.dea = dea;
    }

    public Concept getSpecialtyConcept() {
        return specialtyConcept;
    }

    public void setSpecialtyConcept(Concept specialtyConcept) {
        this.specialtyConcept = specialtyConcept;
    }

    public CareSite getCareSite() {
        return careSite;
    }

    public void setCareSite(CareSite careSite) {
        this.careSite = careSite;
    }

    public Integer getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(Integer yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public String getProviderSourceValue() {
        return providerSourceValue;
    }

    public void setProviderSourceValue(String providerSourceValue) {
        this.providerSourceValue = providerSourceValue;
    }

    public String getSpecialtySourceValue() {
        return specialtySourceValue;
    }

    public void setSpecialtySourceValue(String specialtySourceValue) {
        this.specialtySourceValue = specialtySourceValue;
    }

    @Override
    public Practitioner getRelatedResource() {
        Practitioner practitioner = new Practitioner();
        practitioner.setId(this.getIdDt());
        if (this.getProviderName() != null) {
            HumanNameDt humanName = new HumanNameDt();
            String[] names = this.getProviderName().trim().split(",");
            List<StringDt> familyNames = new ArrayList<StringDt>();
            List<StringDt> givenNames = new ArrayList<StringDt>();
            List<StringDt> suffixes = new ArrayList<StringDt>();
            if (names.length > 1) {
                if (names.length > 2) {
                    familyNames.add(new StringDt(names[0].trim()));
                    suffixes.add(new StringDt(names[names.length - 1].trim()));

                    for (int i = 1; i < names.length - 1; i++) {
                        String[] subnames = names[i].trim().split(" ");
                        for (int j = 0; j < subnames.length; j++) {
                            givenNames.add(new StringDt(subnames[j].trim()));
                        }
                    }
                } else {
                    suffixes.add(new StringDt(names[1].trim()));
                    String[] subnames = names[0].trim().split(" ");
                    familyNames.add(new StringDt(subnames[subnames.length - 1]));
                    for (int i = 0; i < subnames.length - 1; i++) {
                        givenNames.add(new StringDt(subnames[i].trim()));
                    }
                }
            } else {
                names = this.getProviderName().trim().split(" ");
                if (names.length > 0) {
                    familyNames.add(new StringDt(names[names.length - 1].trim()));
                    for (int i = 0; i < names.length - 1; i++) {
                        givenNames.add(new StringDt(names[i].trim()));
                    }
                } else {
                    familyNames.add(new StringDt(this.getProviderName().trim()));
                }
            }
            humanName.setFamily(familyNames);
            humanName.setGiven(givenNames);
            humanName.setSuffix(suffixes);

            practitioner.setName(humanName);
        }
        PractitionerRole practitionerRole = new PractitionerRole();

        if (careSite != null) {
            ResourceReferenceDt organizationResource = new ResourceReferenceDt(careSite.getIdDt());
//			List<ResourceReferenceDt> listResourceRef = new ArrayList<ResourceReferenceDt>();
//			listResourceRef.add(organizationResource);
            organizationResource.setDisplay(careSite.getCareSiteName());
            practitionerRole.setManagingOrganization(organizationResource);
        }

        if (specialtyConcept != null &&
                specialtyConcept.getId() > 0L) {
            String systemUriString = specialtyConcept.getVocabulary().getReference();
            String displayString = specialtyConcept.getName();
            String codeString = specialtyConcept.getConceptCode();

            CodeableConceptDt specialtyCode = new CodeableConceptDt(systemUriString, codeString);
            specialtyCode.getCodingFirstRep().setDisplay(displayString);

            List<CodeableConceptDt> listSpecialtyCode = new ArrayList<CodeableConceptDt>();
            listSpecialtyCode.add(specialtyCode);
            practitionerRole.setSpecialty(listSpecialtyCode);
        }

        List<PractitionerRole> listPracRole = new ArrayList<PractitionerRole>();
        listPracRole.add(practitionerRole);

        practitioner.setPractitionerRole(listPracRole);

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
            practitioner.setGender(admGender);
        }

        if (this.yearOfBirth != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, this.yearOfBirth);
            practitioner.setBirthDate(new DateDt(cal.getTime()));
        }
        return practitioner;
    }

    @Override
    public String getResourceType() {
        return RESOURCE_TYPE;
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
        Practitioner practitioner = (Practitioner) resource;

        HumanNameDt humanName = practitioner.getName();
        String familyName = humanName.getFamilyAsSingleString().replace(" ", "_");
        String givenName = humanName.getGivenAsSingleString();
        String suffixName = humanName.getSuffixAsSingleString();
        this.setProviderName(format( "%s %s %s", givenName, familyName, suffixName));

        IdDt myID = practitioner.getId();
        if (myID != null && myID.getIdPartAsLong() != null && myID.getIdPart() != null) {
            this.setId(myID.getIdPartAsLong());
        } else {
            List<AddressDt> addresses = practitioner.getAddress();
            AddressDt newAddress = (addresses.size() > 0) ? addresses.get(0) : null;
            Long existingID = (newAddress != null) ? ProviderDAO.getInstance().getByNameAndLocation(this, Location.searchByAddressDt(newAddress)) : null;
            if (existingID != null) {
                this.setId(existingID);
            }
        }

        this.genderConcept = new Concept();
        this.genderConcept.setId(OmopConceptMapping.getInstance().get(practitioner.getGender().substring(0, 1), OmopConceptMapping.GENDER));

        Date birthDate = practitioner.getBirthDate();
        if (birthDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(birthDate);
            int year = cal.get(Calendar.YEAR);
            this.setYearOfBirth(year);
        }

        PractitionerRole role = practitioner.getPractitionerRoleFirstRep();
        if (role != null) {
            // Search this organization from care_site table.
            WebApplicationContext myAppCtx = ContextLoaderListener.getCurrentWebApplicationContext();
            EntityManager entityManager = myAppCtx.getBean("myBaseDao", BaseFhirDao.class).getEntityManager();

            ResourceReferenceDt org = role.getManagingOrganization();
            if (org != null) {
                StringDt orgDisplay = org.getDisplay();
                if (orgDisplay != null) {
                    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
                    CriteriaQuery<CareSite> criteria = builder.createQuery(CareSite.class);
                    Root<CareSite> from = criteria.from(CareSite.class);
                    criteria.select(from).where(
                            builder.equal(from.get("careSiteName"), orgDisplay.getValueAsString())
                    );
                    TypedQuery<CareSite> query = entityManager.createQuery(criteria);
                    List<CareSite> results = query.getResultList();
                    CareSite careSite = null;
                    if (results.size() > 0) {
                        careSite = results.get(0);
                    } else {
                        careSite = new CareSite();
                        careSite.setCareSiteName(orgDisplay.getValueAsString());
                    }
                    this.setCareSite(careSite);
                }
            }

            CodeableConceptDt specialty = role.getSpecialtyFirstRep();
            if (specialty != null) {
                CodingDt specialtyCode = specialty.getCodingFirstRep();
                String specialtyDisplay = specialtyCode.getDisplay();
                if (specialtyDisplay != null && !specialtyDisplay.isEmpty()) {
                    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
                    CriteriaQuery<Concept> criteria = builder.createQuery(Concept.class);
                    Root<Concept> from = criteria.from(Concept.class);
                    criteria.select(from).where(
                            builder.equal(from.get("conceptClassId"), OmopConceptMapping.SPECIALTY),
                            builder.equal(from.get("name"), specialtyDisplay)
                    );
                    TypedQuery<Concept> query = entityManager.createQuery(criteria);
                    List<Concept> results = query.getResultList();
                    Concept specialtyConcept = null;
                    if (results.size() > 0) {
                        specialtyConcept = results.get(0);
                        this.setSpecialtyConcept(specialtyConcept);
                    } else {
                        specialtyConcept = new Concept();
                        specialtyConcept.setId(0L);
                    }
                    this.setSpecialtyConcept(specialtyConcept);
                }
            }
        }

        return this;
    }

    @Override
    public String translateSearchParam(String chain) {
        String translatedChain = "";
        if (chain.isEmpty())
            return translatedChain;
        String head = "";
        if (chain.contains(".")) {
            head = chain.substring(0, chain.indexOf("."));
            chain = chain.substring(chain.indexOf(".") + 1, chain.length());
        } else {
            head = chain;
        }
        switch (head) {
            case Patient.SP_ORGANIZATION:
                translatedChain = translatedChain.concat("careSite");
                break;
            default:
                break;
        }
        return translatedChain;
    }

}
