package edu.gatech.i3l.fhir.dstu2.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "death")
public class Death implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @OneToOne
    @JoinColumn(name = "person_id", referencedColumnName = "person_id", foreignKey = @ForeignKey(name = "fpk_death_person"))
    private Person person;

    @Column(name = "death_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deathDate;

    @OneToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "death_type_concept_id", referencedColumnName = "concept_id", foreignKey = @ForeignKey(name = "fpk_death_type_concept"))
    private Concept deathType;

    @OneToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "cause_concept_id", referencedColumnName = "concept_id", foreignKey = @ForeignKey(name = "fpk_death_cause_concept"))
    private Concept causeOfDeath;

    @Column(name = "cause_source_value")
    private String causeSourceValue;

    @OneToOne(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "cause_source_concept_id", referencedColumnName = "concept_id", foreignKey = @ForeignKey(name = "fpk_death_cause_concept_s"))
    private Concept causeSourceConcept;

    public Death() {}

    public Death(final Person person) {
        this.person = person;
    }

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
