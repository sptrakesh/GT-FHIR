package edu.gatech.i3l.fhir.dstu2.entities;

import javax.persistence.*;

/**
 * 
 * @author Myung Choi
 */
@Entity
@Table(name="vocabulary")
@NamedQueries(value = { @NamedQuery( name = "findReferenceById", query = "select reference from Vocabulary v where v.name like '%:value%'")})
public class Vocabulary {
	
	@Id
	@Column(name="vocabulary_id", insertable = false, updatable=false)
	@Access(AccessType.PROPERTY)
	private String id;
	
	@Column(name="vocabulary_name", insertable = false, updatable=false, nullable = false)
	private String name;
	
	@Column(name="vocabulary_reference", insertable = false, updatable=false)
	private String reference;

	@Column(name="vocabulary_version", insertable = false, updatable=false)
	private String version;

	@OneToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name="vocabulary_concept_id", referencedColumnName="concept_id", insertable=false, updatable=false)
	private Concept concept;


	public Vocabulary() {}

	public Vocabulary(String id) {
		this.id = id;
	}
	
	public Vocabulary(String id, String name) {
		this.id = id;
		this.name = name;
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

	public String getReference() {
		return reference;
	}
	
	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getVersion() { return version; }

	public void setVersion(String version) { this.version = version; }

	public Concept getConcept() { return concept; }

	public void setConcept(Concept concept) { this.concept = concept; }

	// FIXME This is FHIR related. We may need to do this in the database. But, for quick
	// initial implementation, we do this. Later, we may extend vocabulary table.
	public String getSystemUri() {
		String uri = this.getReference();
		
		if (name.equalsIgnoreCase("SNOMED")) uri = "http://snomed.info/sct";
		else if (name.equalsIgnoreCase("ICD9CM")) uri = "http://hl7.org/fhir/sid/icd-9-cm";
		else if (name.equalsIgnoreCase("ICD9Proc")) uri = "http://hl7.org/fhir/sid/icd-9-cm/procedure";
		else if (name.equalsIgnoreCase("CPT4")) uri = "http://www.ama-assn.org/go/cpt";
		else if (name.equalsIgnoreCase("HCPCS")) uri = "http://purl.bioontology.org/ontology/HCPCS";
		else if (name.equalsIgnoreCase("LOINC")) uri = "http://loinc.org";
		else if (name.equalsIgnoreCase("RxNorm")) uri = "http://www.nlm.nih.gov/research/umls/rxnorm";
		else if (name.equalsIgnoreCase("UCUM")) uri = "http://unitsofmeasure.org";
		else if (name.equalsIgnoreCase("NDC")) uri = "http://hl7.org/fhir/sid/ndc";
		
		return uri;
	}
	
	public void setIdNameBySystemUri(String uri) {
		if (uri.equalsIgnoreCase("http://snomed.info/sct")) {
			this.name = "SNOMED";
		} else if (uri.equalsIgnoreCase("http://hl7.org/fhir/sname/icd-9-cm")) {
			this.name = "ICD9CM";
		} else if (uri.equalsIgnoreCase("http://hl7.org/fhir/sname/icd-9-cm/procedure")) {
			this.name = "ICD9Proc";
		} else if (uri.equalsIgnoreCase("http://www.ama-assn.org/go/cpt")) {
			this.name = "CPT4";
		} else if (uri.equalsIgnoreCase("http://purl.bioontology.org/ontology/HCPCS")) {
			this.name = "HCPCS";
		} else if (uri.equalsIgnoreCase("http://loinc.org")) {
			this.name = "LOINC";
		} else if (uri.equalsIgnoreCase("http://www.nlm.nih.gov/research/umls/rxnorm")) {
			this.name = "RxNorm";
		} else if (uri.equalsIgnoreCase("http://unitsofmeasure.org")) {
			this.name = "UCUM";
		} else if (uri.equalsIgnoreCase("http://hl7.org/fhir/sname/ndc")) {
			this.name = "NDC";
		} else {
			this.name = "Vocabulary";
		}
	}
}
