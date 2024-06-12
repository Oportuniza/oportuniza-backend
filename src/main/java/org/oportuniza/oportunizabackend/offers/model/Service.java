package org.oportuniza.oportunizabackend.offers.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Service extends Offer {
    private Double price;
}
