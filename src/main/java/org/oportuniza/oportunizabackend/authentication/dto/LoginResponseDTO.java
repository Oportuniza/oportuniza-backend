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
        URL pictureUrl,
        double averageRating,
        int reviewCount,
        String district,
        String county,
        Date createdAt,
        String jwtToken) {
}
