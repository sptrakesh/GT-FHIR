package edu.gatech.i3l.fhir.dstu2.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "concept_relationship")
public class ConceptRelationship {

    @Column(name = "concept_id_1", updatable = false, nullable = false)
    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "concept_id_1", referencedColumnName = "concept_id", insertable = false, updatable = false)
    private Concept concept1;

    @Column(name = "concept_id_2", updatable = false, nullable = false)
    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "concept_id_2", referencedColumnName = "concept_id", insertable = false, updatable = false)
    private Concept concept2;

    @Column(name = "relationship_id", updatable = false, nullable = false)
    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "relationship_id", referencedColumnName = "relationship_id", insertable = false, updatable = false)
    private Relationship relationship;

    @Column(name = "valid_start_date", updatable = false, nullable = false)
    private Date validStartDate;

    @Column(name = "valid_end_date", updatable = false, nullable = false)
    private Date validEndDate;

    @Column(name = "invalid_reason", updatable = false, nullable = false)
    private Character invalidReason;

    public ConceptRelationship() {
        super();
    }

    public ConceptRelationship(Concept concept1, Concept concept2,
                               Relationship relationship, Date validStartDate, Date validEndDate,
                               Character invalidReason) {
        super();
        this.concept1 = concept1;
        this.concept2 = concept2;
        this.relationship = relationship;
        this.validStartDate = validStartDate;
        this.validEndDate = validEndDate;
        this.invalidReason = invalidReason;
    }

    public Concept getConcept1() {
        return concept1;
    }

    public void setConcept1(Concept concept1) {
        this.concept1 = concept1;
    }

    public Concept getConcept2() {
        return concept2;
    }

    public void setConcept2(Concept concept2) {
        this.concept2 = concept2;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public Date getValidStartDate() {
        return validStartDate;
    }

    public void setValidStartDate(Date validStartDate) {
        this.validStartDate = validStartDate;
    }

    public Date getValidEndDate() {
        return validEndDate;
    }

    public void setValidEndDate(Date validEndDate) {
        this.validEndDate = validEndDate;
    }

    public Character getInvalidReason() {
        return invalidReason;
    }

    public void setInvalidReason(Character invalidReason) {
        this.invalidReason = invalidReason;
    }

}
