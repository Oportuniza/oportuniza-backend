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
import org.oportuniza.oportunizabackend.users.service.GoogleCloudStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

@org.springframework.stereotype.Service
public class OfferService {
    private final OfferRepository offerRepository;
    private final GoogleCloudStorageService googleCloudStorageService;


    public OfferService(OfferRepository offerRepository, GoogleCloudStorageService googleCloudStorageService) {
        this.offerRepository = offerRepository;
        this.googleCloudStorageService = googleCloudStorageService;
    }

    public Page<OfferDTO> getAllOffers(String title, Double minPrice, Double maxPrice, Double minSalary, Double maxSalary, String workingModel, String workingRegime, Boolean negotiable, int page, int size) {
        Specification<Offer> spec = Specification.where(null);
        if (title != null && !title.isEmpty()) {
            spec = spec.and(OfferSpecifications.titleContains(title));
        }
        if (minPrice != null) {
            spec = spec.and(OfferSpecifications.priceGreaterThanOrEqual(minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and(OfferSpecifications.priceLessThanOrEqual(maxPrice));
        }

        if (minSalary != null) {
            spec = spec.and(OfferSpecifications.salaryGreaterThanOrEqual(minSalary));
        }

        if (maxSalary != null) {
            spec = spec.and(OfferSpecifications.salaryLessThanOrEqual(maxSalary));
        }

        if (workingModel != null && !workingModel.isEmpty()) {
            spec = spec.and(OfferSpecifications.workingModelEquals(workingModel));
        }

        if (workingRegime != null && !workingRegime.isEmpty()) {
            spec = spec.and(OfferSpecifications.workingRegimeEquals(workingRegime));
        }

        if (negotiable != null) {
            spec = spec.and(OfferSpecifications.negotiableEquals(negotiable));
        }

        return offerRepository.findAll(spec,PageRequest.of(page, size)).map(OfferService::convertToDTO);
    }

    public Offer getOffer(long offerId) throws OfferNotFoundException {
        return offerRepository.findById(offerId).orElseThrow(() -> new OfferNotFoundException(offerId));
    }

    public Page<OfferDTO> getOfferByUserId(long userId, int page, int size) {
        return offerRepository.findOffersByUserId(userId, PageRequest.of(page, size)).map(OfferService::convertToDTO);
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

    public void removeImage(long offerId){
        Offer offer = offerRepository.findById(offerId).orElseThrow(() -> new OfferNotFoundException(offerId));
        googleCloudStorageService.deleteFile(offer.getImageFileName());
        offer.setImageUrl(null);
        offer.setImageFileName(null);
        offerRepository.save(offer);
    }

    public OfferDTO getOfferById(long offerId) throws OfferNotFoundException {
        return convertToDTO(getOffer(offerId));
    }

    public static OfferDTO convertToDTO(Offer offer) {
        if (offer instanceof Service service) {
            return new ServiceDTO(
                    service.getId(),
                    service.getTitle(),
                    service.getDescription(),
                    service.getDistrict(),
                    service.getCounty(),
                    service.isNegotiable(),
                    service.getImageUrl(),
                    service.getImageFileName(),
                    service.getCreatedAt(),
                    service.getPrice()
            );
        } else {
            var job = (Job) offer;
            return new JobDTO(
                    job.getId(),
                    job.getTitle(),
                    job.getDescription(),
                    job.getDistrict(),
                    job.getCounty(),
                    job.isNegotiable(),
                    job.getImageUrl(),
                    job.getImageFileName(),
                    job.getCreatedAt(),
                    job.getSalary(),
                    job.getWorkingModel(),
                    job.getWorkingRegime()
            );
        }
    }

}
