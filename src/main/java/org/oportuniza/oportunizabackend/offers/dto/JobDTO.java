package org.oportuniza.oportunizabackend.offers.dto;

import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.Date;

@Getter
@Setter
public class JobDTO extends OfferDTO{
    private double salary;
    private String district;
    private String county;
    private String workingModel;
    private String workingRegime;
    private final String type = "job";

    public JobDTO(long id, String title, String description, boolean negotiable, URL image, String imageFileName, Date createdAt, double salary, String district, String county, String workingModel, String workingRegime) {
        super(id, title, description, negotiable, image, imageFileName, createdAt);
        this.salary = salary;
        this.district = district;
        this.county = county;
        this.workingModel = workingModel;
        this.workingRegime = workingRegime;
    }

    public JobDTO(long id, String title, String description, boolean negotiable, URL image, String imageFileName, double salary, String district, String county, String workingModel, String workingRegime) {
        super(id, title, description, image, imageFileName, negotiable);
        this.salary = salary;
        this.district = district;
        this.county = county;
        this.workingModel = workingModel;
        this.workingRegime = workingRegime;
    }

    public JobDTO() {
    }

}
