package org.oportuniza.oportunizabackend.offers.dto;


import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.Date;

@Getter
@Setter
public class ServiceDTO extends OfferDTO {
    double price;
    private final String type = "service";

    public ServiceDTO(long id, String title, String description, String district, String county, boolean negotiable, URL image, String imageFileName, Date createdAt, double price) {
        super(id, title, description, district, county, negotiable, image, imageFileName, createdAt);
        this.price = price;
    }

    public ServiceDTO(long id, String title, String description, String district, String county, boolean negotiable, URL image, String imageFileName, double price) {
        super(id, title, description, district, county, image, imageFileName, negotiable);
        this.price = price;
    }

    public ServiceDTO() {
    }

}
