package org.oportuniza.oportunizabackend.users.dto;

public record ReviewDTO (
        long id,
        long reviewerId,
        long reviewedId,
        int rating
){
}
