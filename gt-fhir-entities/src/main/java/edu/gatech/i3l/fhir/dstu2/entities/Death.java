package edu.gatech.i3l.fhir.dstu2.entities;

import javax.persistence.*;
import java.sql.Date;


@Entity
@Table(name = "death")
public class Death {

    @Column(name = "person_id", updatable = false, nullable = false)
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "person_id", referencedColumnName = "person_id")
    private Person person;

    @Column(name = "death_date", nullable = false)
    private Date deathDate;

    @Column(name = "death_type_concept_id", nullable = false)
    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "death_type_concept_id", referencedColumnName = "concept_id")
    private Concept deathType;

    @Column(name = "cause_concept_id")
    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "cause_concept_id", referencedColumnName = "concept_id")
    private Concept causeOfDeath;

    @Column(name = "cause_source_value")
    private String causeSourceValue;

    @Column(name = "cause_source_concept_id")
    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "cause_source_concept_id", referencedColumnName = "concept_id")
    private Concept causeSourceConcept;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Date getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }

    public Concept getDeathType() {
        return deathType;
    }

    public void setDeathType(Concept deathType) {
        this.deathType = deathType;
    }

    public Concept getCauseOfDeath() {
        return causeOfDeath;
    }

    public void setCauseOfDeath(Concept causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    public String getCauseSourceValue() {
        return causeSourceValue;
    }

    public void setCauseSourceValue(String causeSourceValue) {
        this.causeSourceValue = causeSourceValue;
    }

    public Concept getCauseSourceConcept() {
        return causeSourceConcept;
    }

    public void setCauseSourceConcept(Concept causeSourceConcept) {
        this.causeSourceConcept = causeSourceConcept;
    }

}
