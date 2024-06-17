package org.oportuniza.oportunizabackend.offers.dto;

public record UpdateServiceDTO (
        String title,
        String description,
        String district,
        String county,
        Double price,
        Boolean negotiable
) {
}
