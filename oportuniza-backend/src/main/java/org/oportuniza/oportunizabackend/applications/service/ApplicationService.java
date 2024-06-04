package org.oportuniza.oportunizabackend.applications.service;

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

    public List<Application> getApplicationsByUserId(Long userId) {
        return applicationRepository.findByUserId(userId);
    }

    public List<Application> getApplicationsByOfferId(Long offerId) {
        return applicationRepository.findByOfferId(offerId);
    }

    public Application getApplicationById(Long id) {
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
            documentRepository.save(document);
            app.addDocument(document);
        }
        applicationRepository.save(app);
        return app;
    }

    public Application acceptApplication(Long id) {
        var app = getApplicationById(id);
        app.setStatus("Accepted");
        applicationRepository.save(app);
        return app;
    }

    public Application rejectApplication(Long id) {
        var app = getApplicationById(id);
        app.setStatus("Rejected");
        applicationRepository.save(app);
        return app;
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }
}
