package edu.gatech.i3l.fhir.dstu2.entities.keys;

import edu.gatech.i3l.fhir.dstu2.entities.PersonComplement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

import static java.lang.String.format;

/**
 * Created by rakesh.vidyadharan on 6/14/17.
 */
public class PersonIdentifierPK implements Serializable {
    private PersonComplement person;
    private String system;
    private String value;

    public PersonIdentifierPK() {}

    public PersonIdentifierPK(final PersonComplement person, final String system, final String value) {
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
        if(!(obj instanceof PersonIdentifierPK)) return false;

        final PersonIdentifierPK other = (PersonIdentifierPK) obj;
        return new EqualsBuilder()
                .append(person.getId(), other.person.getId())
                .append(system, other.system)
                .append(value, other.value)
                .isEquals();
    }

    @Override
    public String toString() {
        return format("PersonIdentifierPK - personId: (%s), system: (%s), value: (%s)", person.getId(), system, value);
    }

    public PersonComplement getPerson() {
        return person;
    }

    public void setPerson(final PersonComplement person) {
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
