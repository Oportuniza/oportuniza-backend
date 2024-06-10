package org.oportuniza.oportunizabackend.offers.service;

import org.oportuniza.oportunizabackend.applications.model.Application;
import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.oportuniza.oportunizabackend.offers.dto.OfferDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.offers.exceptions.OfferNotFoundException;
import org.oportuniza.oportunizabackend.offers.model.Job;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.offers.model.Service;
import org.oportuniza.oportunizabackend.offers.repository.OfferRepository;
import org.oportuniza.oportunizabackend.offers.service.specifications.OfferSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@org.springframework.stereotype.Service
public class OfferService {
    private final OfferRepository offerRepository;


    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public Page<OfferDTO> getAllOffers(String title, int page, int size) {
        Specification<Offer> spec = Specification.where(null);
        if (title != null && !title.isEmpty()) {
            spec = spec.and(OfferSpecifications.titleContains(title));
        }

        return offerRepository.findAll(spec,PageRequest.of(page, size)).map(OfferService::convertToDTO);
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

    public static OfferDTO convertToDTO(Offer offer) {
        if (offer instanceof Service service) {
            return new ServiceDTO(
                    service.getId(),
                    service.getTitle(),
                    service.getDescription(),
                    service.isNegotiable(),
                    service.getPrice()
            );
        } else {
            var job = (Job) offer;
            return new JobDTO(
                    job.getId(),
                    job.getTitle(),
                    job.getDescription(),
                    job.isNegotiable(),
                    job.getSalary(),
                    job.getLocalization(),
                    job.getWorkingModel(),
                    job.getWorkingRegime()
            );
        }
    }

}
