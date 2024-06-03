package org.oportuniza.oportunizabackend.authentication.dto;

public record RegisterResponseDTO(
        String email,
        String name,
        String phoneNumber) {
}
