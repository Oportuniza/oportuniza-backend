package org.oportuniza.oportunizabackend.applications.dto;

public record CreateApplicationDTO(
        String firstName,
        String lastName,
        String email,
        String message
) {
}
