package edu.gatech.i3l.fhir.dstu2.entities;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.*;

/**
 * Created by rakesh.vidyadharan on 4/25/17.
 */
@Entity
@Table(name = "organization")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class Organization {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="organization_id", updatable=false)
    @Access(AccessType.PROPERTY)
    private Long id;

    @ManyToOne(cascade={CascadeType.MERGE})
    @JoinColumn(name="place_of_service_concept_id")
    private Concept placeOfServiceConcept;

    @ManyToOne(cascade={CascadeType.MERGE})
    @JoinColumn(name="location_id")
    private Location location;

    @Column(name="organization_source_value", nullable = false)
    private String sourceValue;

    @Column(name="place_of_service_source_value")
    private String placeOfServiceSourceValue;

    public Organization( Long id, String sourceValue ) {
        this.id = id;
        this.sourceValue = sourceValue;
    }

    public Long getId() { return id; }

    public void setId(Long id) {
        this.id = id;
    }

    public Concept getPlaceOfServiceConcept() { return placeOfServiceConcept; }

    public void setPlaceOfServiceConcept(Concept placeOfServiceConcept) {
        this.placeOfServiceConcept = placeOfServiceConcept;
    }

    public Location getLocation() { return location; }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getSourceValue() { return sourceValue; }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }

    public String getPlaceOfServiceSourceValue() { return placeOfServiceSourceValue; }

    public void setPlaceOfServiceSourceValue(String placeOfServiceSourceValue) {
        this.placeOfServiceSourceValue = placeOfServiceSourceValue;
    }
}
