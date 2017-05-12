package edu.gatech.i3l.fhir.dstu2.entities;

import javax.persistence.*;

@Entity
@Table(name = "concept_synonym")
public class ConceptSynonym {

    @Column(name = "concept_id", updatable = false, nullable = false)
    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "concept_id", referencedColumnName = "concept_id", insertable = false, updatable = false)
    private Concept concept;

    @Column(name = "concept_synonym_name", updatable = false, nullable = false)
    private String name;

    @Column(name = "language_concept_id", updatable = false, nullable = false)
    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "language_concept_id", referencedColumnName = "concept_id", insertable = false, updatable = false)
    private Concept language;

    public ConceptSynonym() {
        super();
    }

    public ConceptSynonym(Concept concept, String name) {
        super();
        this.concept = concept;
        this.name = name;
    }

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Concept getLanguage() {
        return language;
    }

    public void setLanguage(Concept language) {
        this.language = language;
    }
}
