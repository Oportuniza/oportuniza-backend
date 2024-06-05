package org.oportuniza.oportunizabackend.users.dto;

public record UserDTO (
        long id,
        String email,
        String name,
        String phoneNumber,
        String district,
        String county) {
}

