package org.oportuniza.oportunizabackend.authentication.dto;

public record GoogleDTO (
        String email,
        String name,
        String pictureUrl
) {
}
