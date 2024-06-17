package org.oportuniza.oportunizabackend.offers.dto;

public record UpdateServiceDTO (
        String title,
        String description,
        Double price,
        Boolean negotiable
) {
}
