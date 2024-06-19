package org.oportuniza.oportunizabackend.users.exceptions;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(long reviewerId, long reviewedId) {
        super(String.format("Review not found for reviewerId: %d and reviewedId: %d", reviewerId, reviewedId));
    }
}
