package org.oportuniza.oportunizabackend.offers.model;

import jakarta.persistence.Entity;

@Entity
public class Job extends Offer {
    private Double salary;
    private String localization;
}
