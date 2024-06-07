package org.oportuniza.oportunizabackend.applications.service;

import org.oportuniza.oportunizabackend.applications.dto.ApplicationDTO;
import org.oportuniza.oportunizabackend.applications.dto.CreateApplicationDTO;
import org.oportuniza.oportunizabackend.applications.exceptions.ApplicationNotFoundException;
import org.oportuniza.oportunizabackend.applications.model.Application;
import org.oportuniza.oportunizabackend.applications.model.Document;
import org.oportuniza.oportunizabackend.applications.repository.ApplicationRepository;
import org.oportuniza.oportunizabackend.applications.repository.DocumentRepository;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.users.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final DocumentRepository documentRepository;

    public ApplicationService(final ApplicationRepository applicationRepository, DocumentRepository documentRepository) {
        this.applicationRepository = applicationRepository;
        this.documentRepository = documentRepository;
    }

    public List<ApplicationDTO> getApplicationsByUserId(Long userId) {
        return applicationRepository.findByUserId(userId).stream().map(this::convertToDTO).toList();
    }

    public List<ApplicationDTO> getApplicationsByOfferId(Long offerId) {
        return applicationRepository.findByOfferId(offerId).stream().map(this::convertToDTO).toList();
    }

    public ApplicationDTO getApplicationById(Long id) throws ApplicationNotFoundException {
        return convertToDTO(applicationRepository.findById(id).orElseThrow(() -> new ApplicationNotFoundException(id)));
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

    public Application rejectApplication(long id) {
        var app = getApplication(id);
        app.setStatus("Rejected");
        applicationRepository.save(app);
        return app;
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
                application.getStatus());
    }

    public Application getApplication(Long id) throws ApplicationNotFoundException {
        return applicationRepository.findById(id).orElseThrow(() -> new ApplicationNotFoundException(id));
    }

    private boolean applicationExists(long id) {
        return applicationRepository.existsById(id);
    }
}
