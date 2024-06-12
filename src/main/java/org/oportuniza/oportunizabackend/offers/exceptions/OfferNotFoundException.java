package org.oportuniza.oportunizabackend.offers.exceptions;

public class OfferNotFoundException extends RuntimeException {
    public OfferNotFoundException(long offerId) {
        super("Offer with id " + offerId + " not found.");
    }
}
