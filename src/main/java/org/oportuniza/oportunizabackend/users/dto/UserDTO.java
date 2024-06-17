package org.oportuniza.oportunizabackend.users.dto;

import java.net.URL;
import java.util.Date;

public record UserDTO (
        long id,
        String email,
        String name,
        String phoneNumber,
        String district,
        String county,
        URL resumeUrl,
        URL pictureUrl,
        Double rating,
        int reviewsCount,
        Date createdAt) {
}

