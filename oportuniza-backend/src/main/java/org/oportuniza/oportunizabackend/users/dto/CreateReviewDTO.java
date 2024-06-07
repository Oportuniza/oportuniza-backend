package org.oportuniza.oportunizabackend.users.dto;

public record CreateReviewDTO (
        long reviewerId,
        long reviewedId,
        int rating
) {
}
