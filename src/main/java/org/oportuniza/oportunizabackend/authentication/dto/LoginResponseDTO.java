package org.oportuniza.oportunizabackend.authentication.dto;

import java.util.List;

public record LoginResponseDTO(
        long id,
        String email,
        List<String> roles,
        String name,
        String phoneNumber,
        String resumeUrl,
        String pictureUrl,
        double averageRating,
        int reviewCount,
        String district,
        String county,
        String jwtToken) {
}
