package org.oportuniza.oportunizabackend.applications.service;

import org.oportuniza.oportunizabackend.applications.dto.ApplicationDTO;
import org.oportuniza.oportunizabackend.applications.mapper.ApplicationMapper;
import org.oportuniza.oportunizabackend.applications.model.Application;
import org.oportuniza.oportunizabackend.applications.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(final ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public List<ApplicationDTO> getApplicationsByUserId(Long userId) {
        return applicationRepository.findByUserId(userId).stream()
                .map(ApplicationMapper.INSTANCE::applicationToApplicationDTO)
                .collect(Collectors.toList());
    }

    public List<ApplicationDTO> getApplicationsByOfferId(Long offerId) {
        return applicationRepository.findByOfferId(offerId).stream()
                .map(ApplicationMapper.INSTANCE::applicationToApplicationDTO)
                .collect(Collectors.toList());
    }

    public ApplicationDTO getApplicationById(Long id) {
        Application application = applicationRepository.findById(id).orElse(null);
        return application != null ? ApplicationMapper.INSTANCE.applicationToApplicationDTO(application) : null;
    }

    public ApplicationDTO createApplication(ApplicationDTO applicationDTO) {
        Application application = ApplicationMapper.INSTANCE.applicationDTOToApplication(applicationDTO);
        application = applicationRepository.save(application);
        return ApplicationMapper.INSTANCE.applicationToApplicationDTO(application);
    }

    public ApplicationDTO scheduleInterview(Long id) {
        Application application = applicationRepository.findById(id).orElse(null);
        if (application == null) {
            return null;
        }
        // application.setInterviewScheduled(true);
        // application = applicationRepository.save(application);
        return ApplicationMapper.INSTANCE.applicationToApplicationDTO(application);
    }

    public ApplicationDTO acceptApplication(Long id) {
        Application application = applicationRepository.findById(id).orElse(null);
        if (application == null) {
            return null;
        }
        // application.setStatus("Accepted");
        // application = applicationRepository.save(application);
        return ApplicationMapper.INSTANCE.applicationToApplicationDTO(application);
    }

    public ApplicationDTO rejectApplication(Long id) {
        Application application = applicationRepository.findById(id).orElse(null);
        if (application == null) {
            return null;
        }
        // application.setStatus("Rejected");
        // application = applicationRepository.save(application);
        return ApplicationMapper.INSTANCE.applicationToApplicationDTO(application);
    }
}
