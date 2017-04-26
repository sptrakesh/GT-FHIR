package edu.gatech.i3l.fhir.dstu2.entities;

import javax.persistence.*;

@Entity
@Table(name="domain")
public class Domain {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="domain_id", updatable = false)
	private String domainId;
	
	@Column(name="domain_name")
	private String domainName;
	
	@ManyToOne(cascade={CascadeType.MERGE})
	@JoinColumn(name="domain_concept_id")
	private Concept domainConcept;
	
	public Domain() {
		super();
	}
	
	public Domain(String domainId, String domainName, Concept domainConcept) {
		super();
		this.domainId = domainId;
		this.domainName = domainName;
		this.domainConcept = domainConcept;
	}
	
	public String getDomainId() {
		return domainId;
	}
	
	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}
	
	public String getDomainName() {
		return domainName;
	}
	
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	public Concept getDomainConcept() {
		return domainConcept;
	}
	
	public void setDomainConcept(Concept domainConcept) {
		this.domainConcept = domainConcept;
	}
}
