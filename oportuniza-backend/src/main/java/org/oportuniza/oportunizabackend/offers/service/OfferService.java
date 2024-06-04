package org.oportuniza.oportunizabackend.offers.service;

import org.oportuniza.oportunizabackend.offers.exceptions.OfferNotFoundException;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.offers.repository.OfferRepository;
import org.springframework.stereotype.Service;

@Service
public class OfferService {
    private final OfferRepository offerRepository;


    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public Offer getOffer(long offerId) {
        return offerRepository.findById(offerId).orElseThrow(() -> new OfferNotFoundException(offerId));
    }

}
