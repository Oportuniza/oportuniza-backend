package org.oportuniza.oportunizabackend.applications.service;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.oportuniza.oportunizabackend.applications.dto.ApplicationDTO;
import org.oportuniza.oportunizabackend.applications.dto.CreateApplicationDTO;
import org.oportuniza.oportunizabackend.applications.exceptions.ApplicationNotFoundException;
import org.oportuniza.oportunizabackend.applications.model.Application;
import org.oportuniza.oportunizabackend.applications.model.Document;
import org.oportuniza.oportunizabackend.applications.repository.ApplicationRepository;
import org.oportuniza.oportunizabackend.offers.model.Job;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.service.GoogleCloudStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;


@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final GoogleCloudStorageService googleCloudStorageService;

    public ApplicationService(final ApplicationRepository applicationRepository, GoogleCloudStorageService googleCloudStorageService) {
        this.applicationRepository = applicationRepository;
        this.googleCloudStorageService = googleCloudStorageService;
    }

    public Page<ApplicationDTO> getApplicationsByUserId(long userId, int page, int size) {
        return applicationRepository.findByUserId(userId, PageRequest.of(page, size)).map(a -> {
            try {
                return convertToDTO(a);
            } catch (MalformedURLException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Page<ApplicationDTO> getApplicationsByOfferId(long offerId, int page, int size) {
        return applicationRepository.findByOfferId(offerId, PageRequest.of(page, size)).map(a -> {
            try {
                return convertToDTO(a);
            } catch (MalformedURLException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Application getApplicationById(long id) throws ApplicationNotFoundException {
        return applicationRepository.findById(id).orElseThrow(() -> new ApplicationNotFoundException(id));
    }

    public Application createApplication(CreateApplicationDTO applicationDTO, Offer offer, User user, MultipartFile resume, MultipartFile[] files) throws IOException, URISyntaxException {
        var app = new Application();
        app.setOffer(offer);
        app.setUser(user);
        app.setFirstName(applicationDTO.firstName());
        app.setLastName(applicationDTO.lastName());
        app.setEmail(applicationDTO.email());
        app.setMessage(applicationDTO.message());
        app.setStatus("Pending");
        app.setPhoneNumber(applicationDTO.phoneNumber());
        app.setResumeFileName(applicationDTO.resumeFileName());
        if (resume != null && !resume.isEmpty()) {
            var resumeUrl = googleCloudStorageService.uploadFile(resume);
            app.setResumeUrl(resumeUrl.getValue1());
            app.setResumeNameInBucket(resumeUrl.getValue0());
        } else {
            app.setResumeNameInBucket(applicationDTO.resumeNameInBucket());
            app.setResumeUrl(new URI(applicationDTO.resumeUrl()).toURL());
        }

        var filesNames = applicationDTO.documentsFilesNames();
        if (files != null && filesNames != null && files.length == filesNames.size()) {
            for (int i = 0; i < files.length; i++) {
                var file = files[i];
                var fileName = filesNames.get(i);
                if (file != null && !file.isEmpty()) {
                    var documentUrl = googleCloudStorageService.uploadFile(file);
                    var document = new Document();
                    document.setUrl(documentUrl.getValue1());
                    document.setNameInBucket(documentUrl.getValue0());
                    document.setFileName(fileName);
                    document.setApplication(app);
                    app.addDocument(document);
                }
            }
        }

        applicationRepository.save(app);
        return app;
    }

    public Application acceptApplication(long id) {
        var app = getApplication(id);
        var offer = app.getOffer();
        if (offer instanceof Job job) {
            for (Application application : job.getApplications()) {
                if (application.getStatus().equals("Accepted")) {
                    application.setStatus("Rejected");
                    applicationRepository.save(application);
                }
            }
        }
        app.setStatus("Accepted");
        applicationRepository.save(app);
        return app;
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

    public ApplicationDTO convertToDTO(Application application) throws MalformedURLException, URISyntaxException {
        return new ApplicationDTO(
                application.getId(),
                application.getOffer().getId(),
                application.getUser().getId(),
                application.getFirstName(),
                application.getLastName(),
                application.getPhoneNumber(),
                application.getEmail(),
                application.getMessage(),
                application.getResumeUrl(),
                application.getResumeNameInBucket(),
                application.getResumeFileName(),
                application.getDocuments().stream().map(doc -> new Triplet<>(doc.getFileName(), doc.getNameInBucket(), doc.getUrl())).toList(),
                application.getStatus(),
                application.getCreatedAt());
    }

    public Application getApplication(Long id) throws ApplicationNotFoundException {
        return applicationRepository.findById(id).orElseThrow(() -> new ApplicationNotFoundException(id));
    }

    public void removeOfferApplications(Offer offer) {
        applicationRepository.deleteAll(offer.getApplications());
    }

    private boolean applicationExists(long id) {
        return applicationRepository.existsById(id);
    }
}
