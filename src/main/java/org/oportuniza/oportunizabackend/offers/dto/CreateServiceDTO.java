package org.oportuniza.oportunizabackend.offers.dto;

public record CreateServiceDTO(
        String title,
        String description,
        String district,
        String county,
        boolean negotiable,
        double price
) {
}