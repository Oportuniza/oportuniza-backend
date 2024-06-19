package org.oportuniza.oportunizabackend.authentication.dto;

import java.net.URL;
import java.util.Date;
import java.util.List;

public record LoginResponseDTO(
        long id,
        String email,
        List<String> roles,
        String name,
        String phoneNumber,
        URL resumeUrl,
        String resumeName,
        String resumeFileName,
        URL pictureUrl,
        String pictureName,
        double averageRating,
        int reviewCount,
        String district,
        String county,
        Date lastLogin,
        Date createdAt,
        String jwtToken) {
}
