package org.oportuniza.oportunizabackend.offers.dto;

import org.oportuniza.oportunizabackend.users.dto.UserDTO;

import java.net.URL;
import java.util.Date;

public record GetJobDTO (
        long id,
        String title,
        String description,
        URL image,
        String imageName,
        boolean negotiable,
        Date createdAt,
        double salary,
        String district,
        String county,
        String workingModel,
        String workingRegime,
        UserDTO userDTO)
    {
}
