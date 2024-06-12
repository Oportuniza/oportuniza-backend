package org.oportuniza.oportunizabackend.applications.dto;

import java.util.List;

public record CreateApplicationDTO(
        String firstName,
        String lastName,
        String email,
        String message,
        String resumeUrl,
        List<String> documentsUrls
) {
}
