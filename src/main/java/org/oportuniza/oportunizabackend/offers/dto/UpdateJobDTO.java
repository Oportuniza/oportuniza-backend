package org.oportuniza.oportunizabackend.offers.dto;

public record UpdateJobDTO (
        String title,
        String description,
        Boolean negotiable,
        Double salary,
        String district,
        String county,
        String workingModel,
        String workingRegime
){
}
