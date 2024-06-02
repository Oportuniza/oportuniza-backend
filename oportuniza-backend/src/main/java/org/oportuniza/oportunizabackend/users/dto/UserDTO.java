package org.oportuniza.oportunizabackend.users.dto;

import org.oportuniza.oportunizabackend.users.model.Role;

public record UserDTO (
        String email,
        String name,
        String phoneNumber,
        String district,
        String county) {
}

