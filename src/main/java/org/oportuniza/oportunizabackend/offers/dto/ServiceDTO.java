package org.oportuniza.oportunizabackend.offers.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ServiceDTO extends OfferDTO {
    double price;
    private final String type = "service";

    public ServiceDTO(long id, String title, String description, boolean negotiable, Date createdAt, double price) {
        super(id, title, description, negotiable, createdAt);
        this.price = price;
    }

    public ServiceDTO(long id, String title, String description, boolean negotiable, double price) {
        super(id, title, description, negotiable);
        this.price = price;
    }

    public ServiceDTO() {
    }

}
