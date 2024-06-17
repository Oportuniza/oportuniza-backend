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
        String resumeName,
        URL pictureUrl,
        String pictureName,
        Double rating,
        int reviewsCount,
        Date lastLogin,
        Date createdAt) {
}

