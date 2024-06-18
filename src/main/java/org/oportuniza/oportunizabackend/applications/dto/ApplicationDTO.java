package org.oportuniza.oportunizabackend.applications.dto;

import org.javatuples.Triplet;

import java.net.URL;
import java.util.Date;
import java.util.List;

public record ApplicationDTO (
        long id,
        long offerId,
        long userId,
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        String message,
        URL resumeUrl,
        String resumeBucketName,
        String resumeFileName,
        List<Triplet<String, String, URL>> documentsUrls,
        String status,
        Date createdAt
){
}
