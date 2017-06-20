package edu.gatech.i3l.fhir.dstu2.entities;

import edu.gatech.i3l.fhir.jpa.entity.BaseResourceEntity;
import org.hibernate.annotations.DiscriminatorFormula;

import javax.persistence.*;


@Entity
@Table(name = "drug_exposure")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("case when drug_type_concept_id > 0 then 'DrugStatement' end")
public abstract class DrugExposure extends BaseResourceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drug_exposure_id", updatable = false)
    @Access(AccessType.PROPERTY)
    private Long id;

    @Column(name = "effective_drug_dose")
    private Double effectiveDrugDose;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "dose_unit_concept_id", foreignKey = @ForeignKey(name = "fpk_drug_dose_unit_concept"))
    private Concept doseUnitConcept;

    @Column(name = "drug_source_value")
    private String drugSourceValue;

    @Column(name = "dose_unit_source_value")
    private String doseUnitSourceValue;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getEffectiveDrugDose() {
        return effectiveDrugDose;
    }

    public void setEffectiveDrugDose(Double effectiveDrugDose) {
        this.effectiveDrugDose = effectiveDrugDose;
    }

    public Concept getDoseUnitConcept() {
        return doseUnitConcept;
    }

    public void setDoseUnitConcept(Concept doseUnitConcept) {
        this.doseUnitConcept = doseUnitConcept;
    }

    public String getDrugSourceValue() {
        return drugSourceValue;
    }

    public void setDrugSourceValue(String drugSourceValue) {
        this.drugSourceValue = drugSourceValue;
    }

    public String getDoseUnitSourceValue() {
        return doseUnitSourceValue;
    }

    public void setDoseUnitSourceValue(String doseUnitSourceValue) {
        this.doseUnitSourceValue = doseUnitSourceValue;
    }
}
