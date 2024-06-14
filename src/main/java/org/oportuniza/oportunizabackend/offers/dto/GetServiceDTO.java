package org.oportuniza.oportunizabackend.offers.dto;

import org.oportuniza.oportunizabackend.users.dto.UserDTO;

import java.util.Date;

public record GetServiceDTO (
        long id,
        String title,
        String description,
        boolean negotiable,
        Date createdAt,
        double price,
        UserDTO user
) {
}
