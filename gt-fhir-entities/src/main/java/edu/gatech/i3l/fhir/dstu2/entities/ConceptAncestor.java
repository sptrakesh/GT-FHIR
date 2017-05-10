package edu.gatech.i3l.fhir.dstu2.entities;

import javax.persistence.*;

@Entity
@Table(name="concept_ancestor")
public class ConceptAncestor {

	@Column(name="ancestor_concept_id", updatable=false, nullable = false)
    @OneToOne(cascade={CascadeType.MERGE})
    @JoinColumn(name="ancestor_concept_id", referencedColumnName="concept_id", insertable=false, updatable=false)
	private Concept ancestor;

    @Column(name="descendant_concept_id", updatable=false, nullable = false)
    @OneToOne(cascade={CascadeType.MERGE})
    @JoinColumn(name="descendant_concept_id", referencedColumnName="concept_id", insertable=false, updatable=false)
	private Concept descendant;

    @Column(name="min_levels_of_separation", updatable=false, nullable = false)
	private Integer minLevelsOfSeparation;

    @Column(name="max_levels_of_separation", updatable=false, nullable = false)
	private Integer maxLevelsOfSeparation;

	public ConceptAncestor() {
		super();
	}

	public ConceptAncestor(Concept ancestor, Concept descendant,
			Integer minLevelsOfSeparation, Integer maxLevelsOfSeparation) {
		super();
		this.ancestor = ancestor;
		this.descendant = descendant;
		this.minLevelsOfSeparation = minLevelsOfSeparation;
		this.maxLevelsOfSeparation = maxLevelsOfSeparation;
	}

	public Concept getAncestor() {
		return ancestor;
	}

	public void setAncestor(Concept ancestor) {
		this.ancestor = ancestor;
	}

	public Concept getDescendant() {
		return descendant;
	}

	public void setDescendant(Concept descendant) {
		this.descendant = descendant;
	}

	public Integer getMinLevelsOfSeparation() {
		return minLevelsOfSeparation;
	}

	public void setMinLevelsOfSeparation(Integer minLevelsOfSeparation) {
		this.minLevelsOfSeparation = minLevelsOfSeparation;
	}

	public Integer getMaxLevelsOfSeparation() {
		return maxLevelsOfSeparation;
	}

	public void setMaxLevelsOfSeparation(Integer maxLevelsOfSeparation) {
		this.maxLevelsOfSeparation = maxLevelsOfSeparation;
	}

}
