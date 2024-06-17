package org.oportuniza.oportunizabackend.offers.dto;

import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.Date;

@Getter
@Setter
public abstract class OfferDTO {
    private long id;
    private String title;
    private String description;
    private String district;
    private String county;
    private boolean negotiable;
    private URL image;
    private String imageFileName;
    private Date createdAt;

    public OfferDTO(long id, String title, String description, String district, String county, boolean negotiable, URL image, String imageFileName, Date createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.district = district;
        this.county = county;
        this.negotiable = negotiable;
        this.image = image;
        this.imageFileName = imageFileName;
        this.createdAt = createdAt;
    }

    public OfferDTO(long id, String title, String description, String district, String county, URL image, String imageFileName, boolean negotiable) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.district = district;
        this.county = county;
        this.negotiable = negotiable;
        this.image = image;
        this.imageFileName = imageFileName;
    }

    public OfferDTO() {
    }
}
