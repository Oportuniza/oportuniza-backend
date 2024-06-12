package org.oportuniza.oportunizabackend.applications.dto;

import java.util.List;

public record ApplicationDTO (
        long id,
        long offerId,
        long userId,
        String firstName,
        String lastName,
        String email,
        String message,
        String resumeUrl,
        List<String> documentsUrls,
        String status
){
}
