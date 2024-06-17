package org.oportuniza.oportunizabackend.offers.dto;

import org.oportuniza.oportunizabackend.users.dto.UserDTO;

import java.net.URL;
import java.util.Date;

public record GetServiceDTO (
        long id,
        String title,
        String description,
        String district,
        String county,
        URL image,
        String imageName,
        boolean negotiable,
        Date createdAt,
        double price,
        UserDTO user
) {
}
