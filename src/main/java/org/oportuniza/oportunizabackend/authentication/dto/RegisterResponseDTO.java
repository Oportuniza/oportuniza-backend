package org.oportuniza.oportunizabackend.authentication.dto;

public record RegisterResponseDTO(
        long id,
        String email,
        String name,
        String phoneNumber) {
}
