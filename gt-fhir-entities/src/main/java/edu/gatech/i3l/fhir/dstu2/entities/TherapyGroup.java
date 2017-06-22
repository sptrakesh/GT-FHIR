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
import ca.uhn.fhir.model.primitive.StringDt;
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
@Table(name = "fhir_therapy_group")
public class TherapyGroup extends BaseResourceEntity {
    private static final String RES_TYPE = "Basic";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "therapy_group_id", updatable = false)
    @Access(AccessType.PROPERTY)
    private Long id;

    @Column(name = "therapy_group_name")
    private String name;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false, foreignKey = @ForeignKey(name = "fpk_therapy_group_person"))
    @NotNull
    private PersonComplement person;

    @Column(name = "timeline_sequence_id")
    private Integer sequence;

    @Override
    public IResource getRelatedResource() {
        final Basic basic = new Basic();

        basic.setId(new IdDt(id));
        basic.setSubject(new ResourceReferenceDt(person.getIdDt()));
        writeCode(basic);
        writeExtensions(basic);

        return basic;
    }

    @Override
    public IResourceEntity constructEntityFromResource(IResource resource) {
        if (!(resource instanceof Basic)) return null;

        final Basic basic = (Basic) resource;
        person = readPerson(basic);

        for (final ExtensionDt extension : basic.getAllUndeclaredExtensions()) {
            if (extension.getUrl().equals(ConceptDAO.getInstance().config.getString("entities.Basic.TherapyGroup.url"))) {
                readExtensions(extension);
            } else {
                throw new UnsupportedOperationException(format("Unsupported extension uri: %s", extension.getUrl()));
            }
        }
        return this;
    }

    @Override
    public String translateSearchParam(String theSearchParam) {
        return null;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
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

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PersonComplement getPerson() {
        return person;
    }

    public void setPerson(PersonComplement person) {
        this.person = person;
    }

    private void writeCode(final Basic basic) {
        final ConceptDAO dao = ConceptDAO.getInstance();
        final Config config = ConceptDAO.getInstance().config.getConfig("concept.basic.therapyGroup");
        final String url = dao.getSystemUri(config.getString("url"));

        final CodingDt code = new CodingDt(url, config.getString("code"));
        final CodeableConceptDt ccdt = new CodeableConceptDt();
        ccdt.addCoding(code);
        basic.setCode(ccdt);
    }

    private void writeExtensions(final Basic basic) {
        if ((name == null) && (sequence == null)) return;

        final Config config = ConceptDAO.getInstance().config.getConfig("entities.Basic.TherapyGroup");
        final ExtensionDt edt = new ExtensionDt();
        edt.setUrl(config.getString("url"));
        basic.addUndeclaredExtension(edt);

        if (name != null) {
            final ExtensionDt ext = new ExtensionDt();
            ext.setUrl(config.getString("nameUrl"));
            ext.setValue(new StringDt(name));
            edt.addUndeclaredExtension(ext);
        }

        if (sequence != null) {
            final ExtensionDt ext = new ExtensionDt();
            ext.setUrl(config.getString("sequenceUrl"));
            ext.setValue(new IntegerDt(sequence));
            edt.addUndeclaredExtension(ext);
        }
    }

    private PersonComplement readPerson(final Basic basic) {
        final ResourceReferenceDt subject = basic.getSubject();
        final PersonComplement person = DAO.getInstance().loadEntityById(PersonComplement.class, subject.getReference().getIdPartAsLong());
        if (person == null)
        {
            throw new IllegalArgumentException(format("Required person reference (%s) not found", subject.getReference().getIdPartAsLong()));
        }

        return person;
    }

    private void readExtensions(final ExtensionDt extension) {
        final Config config = ConceptDAO.getInstance().config.getConfig("entities.Basic.TherapyGroup");

        for (final ExtensionDt child : extension.getUndeclaredExtensions()) {
            if (child.getUrl().equals(config.getString("nameUrl"))) {
                name = ((StringDt) child.getValue()).getValue();
            } else if (child.getUrl().equals(config.getString("sequenceUrl"))) {
                sequence = ((IntegerDt) child.getValue()).getValue();
            } else {
                throw new UnsupportedOperationException(format("Unsupported extension uri: %s", extension.getUrl()));
            }
        }
    }
}
