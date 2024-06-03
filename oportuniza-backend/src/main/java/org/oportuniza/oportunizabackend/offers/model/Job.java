package org.oportuniza.oportunizabackend.offers.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Job extends Offer {
    private Double salary;
    private String localization;
}
