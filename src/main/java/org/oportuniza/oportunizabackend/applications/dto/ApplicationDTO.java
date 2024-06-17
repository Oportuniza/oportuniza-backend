package org.oportuniza.oportunizabackend.applications.dto;

import org.javatuples.Pair;

import java.net.URL;
import java.util.Date;
import java.util.List;

public record ApplicationDTO (
        long id,
        long offerId,
        long userId,
        String firstName,
        String lastName,
        String email,
        String message,
        URL resumeUrl,
        String resumeName,
        List<Pair<String, URL>> documentsUrls,
        String status,
        Date createdAt
){
}
