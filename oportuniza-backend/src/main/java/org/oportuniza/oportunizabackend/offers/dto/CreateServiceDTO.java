package org.oportuniza.oportunizabackend.offers.dto;

public record CreateServiceDTO(
        String title,
        String description,
        boolean negotiable,
        double price
) {
}