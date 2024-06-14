package org.oportuniza.oportunizabackend.offers.dto;

import org.oportuniza.oportunizabackend.users.dto.UserDTO;

import java.util.Date;

public record GetJobDTO (
        long id,
        String title,
        String description,
        boolean negotiable,
        Date createdAt,
        double salary,
        String localization,
        String workingModel,
        String workingRegime,
        UserDTO userDTO)
    {
}
