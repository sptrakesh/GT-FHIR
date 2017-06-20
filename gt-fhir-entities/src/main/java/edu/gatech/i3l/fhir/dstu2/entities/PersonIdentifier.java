package edu.gatech.i3l.fhir.dstu2.entities;

import edu.gatech.i3l.fhir.dstu2.entities.keys.PersonIdentifierPK;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by rakesh.vidyadharan on 6/14/17.
 */
@Entity
@IdClass(PersonIdentifierPK.class)
@Table(name = "fhir_person_identifier")
public class PersonIdentifier implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "person_id", foreignKey = @ForeignKey(name = "fpk_person_identifier_person"))
    private PersonComplement person;

    @Id
    @Column(name = "system", updatable = false)
    private String system;

    @Id
    @Column(name = "value", updatable = false)
    private String value;

    public PersonIdentifier() {}

    public PersonIdentifier(final PersonComplement person, final String system, final String value) {
        this.person = person;
        this.system = system;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(person.getId())
                .append(system)
                .append(value)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PersonIdentifier)) return false;

        final PersonIdentifier other = (PersonIdentifier) obj;
        return new EqualsBuilder()
                .append(person.getId(), other.person.getId())
                .append(system, other.system)
                .append(value, other.value)
                .isEquals();
    }

    public PersonComplement getPerson() {
        return person;
    }

    public void setPerson(PersonComplement person) {
        this.person = person;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
