package org.oportuniza.oportunizabackend.users.dto;

public record UpdateUserDTO (
        String email,
        String password,
        String oldPassword,
        String name,
        String phoneNumber,
        String district,
        String county) {
}
