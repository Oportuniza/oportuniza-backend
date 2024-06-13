package org.oportuniza.oportunizabackend.offers.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class JobDTO extends OfferDTO{
    private double salary;
    private String localization;
    private String workingModel;
    private String workingRegime;

    public JobDTO(long id, String title, String description, boolean negotiable, Date createdAt, double salary, String localization, String workingModel, String workingRegime) {
        super(id, title, description, negotiable, createdAt);
        this.salary = salary;
        this.localization = localization;
        this.workingModel = workingModel;
        this.workingRegime = workingRegime;
    }

    public JobDTO(long id, String title, String description, boolean negotiable, double salary, String localization, String workingModel, String workingRegime) {
        super(id, title, description, negotiable);
        this.salary = salary;
        this.localization = localization;
        this.workingModel = workingModel;
        this.workingRegime = workingRegime;
    }

    public JobDTO() {
    }

}
