package org.oportuniza.oportunizabackend.offers.model;

import jakarta.persistence.Entity;

@Entity
public class Service extends Offer {
    private Double price;
}
