package edu.gatech.i3l.fhir.dstu2.entities;

import javax.persistence.*;

@Entity
@Table(name = "relationship")
public class Relationship {

    @Id
    @Column(name = "relationship_id", updatable = false, insertable = false)
    private String id;

    @Column(name = "relationship_name", nullable = false, insertable = false, updatable = false)
    private String name;

	/* 
     * The following attributes could be Boolean types, but they are Characters
	 * to remain in conformance to Omop definition.
	 */
    /**
     * Defines whether a relationship defines concepts into classes or
     * hierarchies. Values are 1 for hierarchical relationship or 0 if not.
     */
    @Column(name = "is_hierarchical", nullable = false, insertable = false, updatable = false)
    private Character isHierarchical;

    /**
     * Defines whether a hierarchical relationship contributes to the
     * concept_ancestor table. These are subsets of the hierarchical
     * relationships. Valid values are 1 or 0.
     */
    @Column(name = "defines_ancestry", nullable = false, insertable = false, updatable = false)
    private Character definesAncestry;

    @Column(name = "reverse_relationship_id", nullable = false, insertable = false, updatable = false)
    private String reverseRelationshipId;

    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "relationship_concept_id", referencedColumnName = "concept_id", insertable = false, updatable = false)
    private Concept relationshipConcept;

    public Relationship() {
        super();
    }

    public Relationship(String id, String name, Character isHierarchical,
                        Character definesAncestry, String reverseRelationshipId, Concept relationshipConcept) {
        super();
        this.id = id;
        this.name = name;
        this.isHierarchical = isHierarchical;
        this.definesAncestry = definesAncestry;
        this.reverseRelationshipId = reverseRelationshipId;
        this.relationshipConcept = relationshipConcept;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Character getIsHierarchical() {
        return isHierarchical;
    }

    public void setIsHierarchical(Character isHierarchical) {
        this.isHierarchical = isHierarchical;
    }

    public Character getDefinesAncestry() {
        return definesAncestry;
    }

    public void setDefinesAncestry(Character definesAncestry) {
        this.definesAncestry = definesAncestry;
    }

    public String getReverseRelationshipId() {
        return reverseRelationshipId;
    }

    public void setReverseRelationshipId(String reverseRelationshipId) {
        this.reverseRelationshipId = reverseRelationshipId;
    }

    public Concept getRelationshipConcept() {
        return relationshipConcept;
    }

    public void setRelationshipConcept(Concept relationshipConcept) {
        this.relationshipConcept = relationshipConcept;
    }
}
