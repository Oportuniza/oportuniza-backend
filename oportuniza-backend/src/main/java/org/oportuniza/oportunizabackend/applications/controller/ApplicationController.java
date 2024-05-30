package org.oportuniza.oportunizabackend.applications.controller;


import org.oportuniza.oportunizabackend.applications.dto.ApplicationDTO;
import org.oportuniza.oportunizabackend.applications.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(final ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // GET applications from a user -> /applications/applicant/:applicantId
    @GetMapping("/user/{applicantId}")
    public List<ApplicationDTO> getApplicationsByApplicantId(@PathVariable Long applicantId) {
        return applicationService.getApplicationsByApplicantId(applicantId);
    }

    // GET applications from a offer -> /applications/offer/:offerId
    @GetMapping("/offer/{offerId}")
    public List<ApplicationDTO> getApplicationsByOfferId(@PathVariable Long offerId) {
        return applicationService.getApplicationsByOfferId(offerId);
    }

    // GET application -> /applications/:id
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable Long id) {
        ApplicationDTO applicationDTO = applicationService.getApplicationById(id);
        if (applicationDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(applicationDTO);
    }

    // POST application -> /applications
    @PostMapping
    public ResponseEntity<ApplicationDTO> createApplication(@RequestBody ApplicationDTO applicationDTO) {
        ApplicationDTO createdApplication = applicationService.createApplication(applicationDTO);
        return ResponseEntity.ok(createdApplication);
    }

    // PATCH schedule an interview -> /applications/:id/schedule
    @PatchMapping("/{id}/schedule")
    public ResponseEntity<ApplicationDTO> scheduleInterview(@PathVariable Long id) {
        ApplicationDTO applicationDTO = applicationService.scheduleInterview(id);
        if (applicationDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(applicationDTO);
    }

    // PATCH accept application -> /applications/:id/accept
    @PatchMapping("/{id}/accept")
    public ResponseEntity<ApplicationDTO> acceptApplication(@PathVariable Long id) {
        ApplicationDTO applicationDTO = applicationService.acceptApplication(id);
        if (applicationDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(applicationDTO);
    }

    // PATCH reject application -> /applications/:id/reject
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApplicationDTO> rejectApplication(@PathVariable Long id) {
        ApplicationDTO applicationDTO = applicationService.rejectApplication(id);
        if (applicationDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(applicationDTO);
    }
}
