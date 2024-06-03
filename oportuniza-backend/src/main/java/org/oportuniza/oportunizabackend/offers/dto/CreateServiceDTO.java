package org.oportuniza.oportunizabackend.offers.dto;

public record CreateServiceDTO(
        Long id,
        String title,
        String description,
        boolean negotiable,
        Double price
) {
}