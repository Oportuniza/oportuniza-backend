package org.oportuniza.oportunizabackend.offers.dto;

public record ServiceDTO(
    Long id,
    String title,
    String description,
    boolean negotiable,
    Double price
) {
}
