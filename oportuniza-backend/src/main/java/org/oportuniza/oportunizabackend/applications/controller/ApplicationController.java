package org.oportuniza.oportunizabackend.applications.controller;


import org.oportuniza.oportunizabackend.applications.dto.ApplicationDTO;
import org.oportuniza.oportunizabackend.applications.dto.CreateApplicationDTO;
import org.oportuniza.oportunizabackend.applications.model.Application;
import org.oportuniza.oportunizabackend.applications.service.ApplicationService;
import org.oportuniza.oportunizabackend.offers.service.OfferService;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserService userService;
    private final OfferService offerService;

    public ApplicationController(final ApplicationService applicationService, UserService userService, OfferService offerService) {
        this.applicationService = applicationService;
        this.userService = userService;
        this.offerService = offerService;
    }

    // GET applications from a user -> /applications/applicant/:userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ApplicationDTO>> getApplicationsByUserId(@PathVariable Long userId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(applicationService.getApplicationsByUserId(userId, page, size));
    }

    // GET applications from an offer -> /applications/offer/:offerId
    @GetMapping("/offer/{offerId}")
    public ResponseEntity<Page<ApplicationDTO>> getApplicationsByOfferId(@PathVariable Long offerId,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(applicationService.getApplicationsByOfferId(offerId, page, size));
    }

    // GET application -> /applications/:id
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    // POST application -> /applications
    @PostMapping("/users/{userId}/offers/{offerId}")
    public ResponseEntity<ApplicationDTO> createApplication(@PathVariable Long userId, @PathVariable Long offerId,@RequestBody CreateApplicationDTO applicationDTO) {
        var user = userService.getUserById(userId);
        var offer = offerService.getOffer(offerId);
        Application createdApplication = applicationService.createApplication(applicationDTO, offer, user);
        userService.addApplication(user, createdApplication);
        offerService.addApplication(offer, createdApplication);
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.convertToDTO(createdApplication));
    }

    // PATCH accept application -> /applications/:id/accept
    @PatchMapping("/{id}/accept")
    public ResponseEntity<ApplicationDTO> acceptApplication(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.acceptApplication(id));
    }

    // PATCH reject application -> /applications/:id/reject
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Application> rejectApplication(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.rejectApplication(id));
    }

    // DELETE application -> /applications/:id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteApplication(@PathVariable Long id) {
        // remove offers and users connections
        var app = applicationService.getApplication(id);
        userService.removeApplication(app);
        offerService.removeApplication(app);
        applicationService.deleteApplication(id);
        return ResponseEntity.ok("Application deleted successfully.");
    }

}
