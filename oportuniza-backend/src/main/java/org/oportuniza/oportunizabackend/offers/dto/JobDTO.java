package org.oportuniza.oportunizabackend.offers.dto;

public record JobDTO(
    Long id,
    String title,
    String description,
    boolean negotiable,
    Double salary,
    String localization
) {
}
