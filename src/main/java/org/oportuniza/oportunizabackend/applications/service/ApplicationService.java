package org.oportuniza.oportunizabackend.applications.service;

import org.oportuniza.oportunizabackend.applications.dto.ApplicationDTO;
import org.oportuniza.oportunizabackend.applications.dto.CreateApplicationDTO;
import org.oportuniza.oportunizabackend.applications.exceptions.ApplicationNotFoundException;
import org.oportuniza.oportunizabackend.applications.model.Application;
import org.oportuniza.oportunizabackend.applications.model.Document;
import org.oportuniza.oportunizabackend.applications.repository.ApplicationRepository;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.users.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(final ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public Page<ApplicationDTO> getApplicationsByUserId(long userId, int page, int size) {
        return applicationRepository.findByUserId(userId, PageRequest.of(page, size)).map(this::convertToDTO);
    }

    public Page<ApplicationDTO> getApplicationsByOfferId(long offerId, int page, int size) {
        return applicationRepository.findByOfferId(offerId, PageRequest.of(page, size)).map(this::convertToDTO);
    }

    public Application getApplicationById(long id) throws ApplicationNotFoundException {
        return applicationRepository.findById(id).orElseThrow(() -> new ApplicationNotFoundException(id));
    }

    public Application createApplication(CreateApplicationDTO applicationDTO, Offer offer, User user) {
        var app = new Application();
        app.setOffer(offer);
        app.setUser(user);
        app.setFirstName(applicationDTO.firstName());
        app.setLastName(applicationDTO.lastName());
        app.setEmail(applicationDTO.email());
        app.setMessage(applicationDTO.message());
        app.setResumeUrl(applicationDTO.resumeUrl());
        app.setStatus("Pending");

        for (var documentUrl : applicationDTO.documentsUrls()) {
            var document = new Document();
            document.setUrl(documentUrl);
            document.setApplication(app);
            app.addDocument(document);
        }

        applicationRepository.save(app);
        return app;
    }

    public ApplicationDTO acceptApplication(long id) {
        var app = getApplication(id);
        app.setStatus("Accepted");
        applicationRepository.save(app);
        return convertToDTO(app);
    }

    public ApplicationDTO rejectApplication(long id) {
        var app = getApplication(id);
        app.setStatus("Rejected");
        applicationRepository.save(app);
        return convertToDTO(app);
    }

    public void deleteApplication(long id) throws ApplicationNotFoundException {
        if (!applicationExists(id)) {
            throw new ApplicationNotFoundException(id);
        }
        applicationRepository.deleteById(id);
    }

    public ApplicationDTO convertToDTO(Application application) {
        return new ApplicationDTO(
                application.getId(),
                application.getOffer().getId(),
                application.getUser().getId(),
                application.getFirstName(),
                application.getLastName(),
                application.getEmail(),
                application.getMessage(),
                application.getResumeUrl(),
                application.getDocuments().stream().map(Document::getUrl).toList(),
                application.getStatus(),
                application.getCreatedAt());
    }

    public Application getApplication(Long id) throws ApplicationNotFoundException {
        return applicationRepository.findById(id).orElseThrow(() -> new ApplicationNotFoundException(id));
    }

    private boolean applicationExists(long id) {
        return applicationRepository.existsById(id);
    }
}
