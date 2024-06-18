package org.oportuniza.oportunizabackend.applications.dto;

import java.util.List;

public record CreateApplicationDTO(
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        String message,
        String resumeUrl,
        String resumeNameInBucket,
        String resumeFileName,
        List<String> documentsFilesNames
) {
}
