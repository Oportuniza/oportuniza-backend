package org.oportuniza.oportunizabackend.offers.dto;

public record CreateJobDTO(
        String title,
        String description,
        boolean negotiable,
        Double salary,
        String district,
        String county,
        String workingModel,
        String workingRegime
) {
}
