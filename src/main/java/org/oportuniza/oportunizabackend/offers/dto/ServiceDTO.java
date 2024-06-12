package org.oportuniza.oportunizabackend.offers.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceDTO extends OfferDTO {
    double price;

    public ServiceDTO(long id, String title, String description, boolean negotiable, double price) {
        super(id, title, description, negotiable);
        this.price = price;
    }
}