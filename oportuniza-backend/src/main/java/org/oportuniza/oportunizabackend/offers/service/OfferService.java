package org.oportuniza.oportunizabackend.offers.service;

import org.oportuniza.oportunizabackend.applications.model.Application;
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

    public void addApplication(Offer offer, Application application) {
        offer.addApplication(application);
        offerRepository.save(offer);
    }

    public void removeApplication(Application application) {
        var offer = application.getOffer();
        offer.removeApplication(application);
        offerRepository.save(offer);
    }

}
