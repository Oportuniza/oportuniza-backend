package org.oportuniza.oportunizabackend.offers.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Job extends Offer {
    private Double salary;
    private String localization;
    @Column(name = "working_model")
    private String workingModel;
    @Column(name = "working_regime")
    private String workingRegime;
}
