package org.oportuniza.oportunizabackend.applications.dto;

import org.oportuniza.oportunizabackend.offers.dto.OfferDTO;
import org.oportuniza.oportunizabackend.users.dto.UserDTO;

import java.util.Date;
import java.util.List;

public record GetApplicationDTO (
        long id,
        String firstName,
        String lastName,
        String email,
        String message,
        String resumeUrl,
        List<String> documentsUrls,
        String status,
        Date createdAt,
        OfferDTO offer,
        UserDTO user
) {
}
