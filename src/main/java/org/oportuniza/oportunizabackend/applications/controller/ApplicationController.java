package org.oportuniza.oportunizabackend.applications.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.javatuples.Triplet;
import org.oportuniza.oportunizabackend.applications.dto.ApplicationDTO;
import org.oportuniza.oportunizabackend.applications.dto.CreateApplicationDTO;
import org.oportuniza.oportunizabackend.applications.dto.GetApplicationDTO;
import org.oportuniza.oportunizabackend.applications.exceptions.ApplicationNotFoundException;
import org.oportuniza.oportunizabackend.applications.model.Application;
import org.oportuniza.oportunizabackend.applications.service.ApplicationService;
import org.oportuniza.oportunizabackend.notifications.services.NotificationService;
import org.oportuniza.oportunizabackend.offers.exceptions.OfferNotFoundException;
import org.oportuniza.oportunizabackend.offers.service.OfferService;
import org.oportuniza.oportunizabackend.users.exceptions.UserNotFoundException;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.oportuniza.oportunizabackend.utils.ErrorResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;


@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserService userService;
    private final OfferService offerService;
    private final NotificationService notificationService;

    public ApplicationController(final ApplicationService applicationService, UserService userService, OfferService offerService, NotificationService notificationService) {
        this.applicationService = applicationService;
        this.userService = userService;
        this.offerService = offerService;
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get applications by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User applications found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public ResponseEntity<Page<ApplicationDTO>> getApplicationsByUserId(
            @Parameter(description = "The ID of the user for which applications are to be retrieved") @PathVariable long userId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(applicationService.getApplicationsByUserId(userId, page, size));
    }

    @GetMapping("/offer/{offerId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get applications by offer id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Offer applications found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public Page<ApplicationDTO> getApplicationsByOfferId(
            @Parameter(description = "The ID of the offer for which applications are to be retrieved") @PathVariable long offerId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {
        return applicationService.getApplicationsByOfferId(offerId, page, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get application by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ApplicationDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Application not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public GetApplicationDTO getApplicationById(
            @Parameter(description = "The ID of the application to be retrieved") @PathVariable long id)
            throws ApplicationNotFoundException {
        var app = applicationService.getApplicationById(id);
        return new GetApplicationDTO(
                app.getId(),
                app.getFirstName(),
                app.getLastName(),
                app.getPhoneNumber(),
                app.getEmail(),
                app.getMessage(),
                app.getResumeUrl(),
                app.getResumeNameInBucket(),
                app.getResumeFileName(),
                app.getDocuments().stream().map(doc -> new Triplet<>(doc.getFileName(), doc.getNameInBucket(), doc.getUrl())).toList(),
                app.getStatus(),
                app.getCreatedAt(),
                OfferService.convertToDTO(app.getOffer()),
                UserService.convertToDTO(app.getUser()),
                UserService.convertToDTO(app.getOffer().getUser())
        );
    }

    @PostMapping("/users/{userId}/offers/{offerId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application created", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ApplicationDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "User or offer not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public ApplicationDTO createApplication(
            @Parameter(description = "The ID of the user who wants to create the application") @PathVariable long userId,
            @Parameter(description = "The ID of the offer for which the application is being created") @PathVariable long offerId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Details for creating the application") @Valid @RequestPart("application") CreateApplicationDTO applicationDTO,
            @RequestPart(value = "resume", required = false) MultipartFile resume,
            @RequestPart(value = "files", required = false) MultipartFile[] files) throws UserNotFoundException, OfferNotFoundException, IOException, URISyntaxException {
        var user = userService.getUserById(userId);
        var offer = offerService.getOffer(offerId);
        notificationService.sendNotification("O seu anúncio \"" + offer.getTitle() + "\" recebeu uma candidatura.", offer.getUser().getId());
        Application createdApplication = applicationService.createApplication(applicationDTO, offer, user, resume, files);
        userService.addApplication(user, createdApplication);
        offerService.addApplication(offer, createdApplication);
        return ApplicationService.convertToDTO(createdApplication);
    }

    @PatchMapping("/{id}/accept")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Accept application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "State of application updated to accepted", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ApplicationDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Application not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public ApplicationDTO acceptApplication(
            @Parameter(description = "The ID of the application to be updated") @PathVariable long id)
            throws ApplicationNotFoundException {
        var app = applicationService.acceptApplication(id);
        notificationService.sendNotification("A sua candidatura ao anúncio \"" + app.getOffer().getTitle() + "\" foi aceite.", app.getUser().getId());
        return ApplicationService.convertToDTO(app);
    }

    @PatchMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Reject application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "State of application updated to rejected", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ApplicationDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Application not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public ApplicationDTO rejectApplication(
            @Parameter(description = "The ID of the application to be updated") @PathVariable long id)
            throws ApplicationNotFoundException {
        var app = applicationService.rejectApplication(id);
        notificationService.sendNotification("A sua candidatura ao anúncio \"" + app.getOffer().getTitle() + "\" foi rejeitada.", app.getUser().getId());
        return ApplicationService.convertToDTO(app);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete application by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application deleted successfully", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            }),
            @ApiResponse(responseCode = "404", description = "Application not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public void deleteApplication(
            @Parameter(description = "The ID of the application to be deleted") @PathVariable long id)
            throws ApplicationNotFoundException {
        // remove offers and users connections
        var app = applicationService.getApplication(id);
        userService.removeApplication(app);
        offerService.removeApplication(app);
        applicationService.deleteApplication(id);
    }

}
