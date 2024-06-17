package org.oportuniza.oportunizabackend.authentication.dto;

public record RegisterDTO(
        String email,
        String password,
        String authProvider,
        String phoneNumber,
        String name,
        String pictureUrl,
        String district,
        String county) {
}
