package org.oportuniza.oportunizabackend.offers.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.applications.service.ApplicationService;
import org.oportuniza.oportunizabackend.offers.dto.CreateServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.GetServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.UpdateServiceDTO;
import org.oportuniza.oportunizabackend.offers.exceptions.ServiceNotFoundException;
import org.oportuniza.oportunizabackend.offers.model.Service;
import org.oportuniza.oportunizabackend.offers.service.ServiceService;
import org.oportuniza.oportunizabackend.users.dto.UserDTO;
import org.oportuniza.oportunizabackend.users.exceptions.UserNotFoundException;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.oportuniza.oportunizabackend.utils.ErrorResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;


@RestController
@RequestMapping("/api/services")
public class ServiceController {
    private final ServiceService serviceService;
    private final UserService userService;
    private final ApplicationService applicationService;

    public ServiceController(ServiceService serviceService, UserService userService, ApplicationService applicationService) {
        this.serviceService = serviceService;
        this.userService = userService;
        this.applicationService = applicationService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all services")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All services obtained", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public Page<ServiceDTO> getAllServices(
            @Parameter(description = "Title of the service") @RequestParam(required = false) String title,
            @Parameter(description = "Minimum price of the service") @RequestParam(required = false) Double minPrice,
            @Parameter(description = "Maximum price of the service") @RequestParam(required = false) Double maxPrice,
            @Parameter(description = "Whether the price is negotiable") @RequestParam(required = false) Boolean negotiable,
            @Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page for pagination") @RequestParam(defaultValue = "10") int size) {
        return serviceService.getAllServices(title, minPrice, maxPrice, negotiable, page, size);
    }

    @GetMapping("/{serviceId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get service by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service obtained", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ServiceDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Service not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public GetServiceDTO getService(
            @Parameter(description = "The ID of the service to be retrieved") @PathVariable long serviceId)
            throws ServiceNotFoundException {
        var service = serviceService.getService(serviceId);

        return new GetServiceDTO(
                service.getId(),
                service.getTitle(),
                service.getDescription(),
                service.getDistrict(),
                service.getCounty(),
                service.getImageUrl(),
                service.getImageFileName(),
                service.isNegotiable(),
                service.getCreatedAt(),
                service.getPrice(),
                userService.convertToDTO(service.getUser()),
                "service");
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get services by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User services found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public Page<ServiceDTO> getUserServices(
            @Parameter(description = "The ID of the user whose services are to be retrieved") @PathVariable long userId,
            @Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page for pagination") @RequestParam(defaultValue = "10") int size) {
        return serviceService.getUserServices(userId, page, size);
    }

    @PutMapping("/{serviceId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service updated", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ServiceDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Service not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public ServiceDTO updateService(
            @Parameter(description = "The ID of the service to be updated") @PathVariable long serviceId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The updated service details")  @RequestPart("service") @Valid UpdateServiceDTO updatedService,
            @RequestPart(value = "image", required = false) MultipartFile image)
            throws ServiceNotFoundException, IOException, URISyntaxException {
        return serviceService.updateService(serviceId, updatedService, image);
    }

    @PostMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Service created", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ServiceDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public ServiceDTO createService(
            @Parameter(description = "The ID of the user creating the service") @PathVariable long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The details of the service to be created") @RequestPart("service") @Valid CreateServiceDTO serviceDTO,
            @RequestPart(value = "image", required = false) MultipartFile image)
            throws UserNotFoundException, IOException, URISyntaxException {
        var user = userService.getUserById(userId);
        var service = serviceService.createService(serviceDTO, user, image);
        userService.addOffer(userId, service);
        return serviceService.convertServiceToServiceDTO(service);
    }

    @DeleteMapping("/{serviceId}") // remove service from user's offers and users' favorites
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service deleted", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            }),
            @ApiResponse(responseCode = "404", description = "Service not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public void deleteService(
            @Parameter(description = "The ID of the service to be deleted") @PathVariable long serviceId)
            throws ServiceNotFoundException {
        Service service = serviceService.getServiceById(serviceId);
        applicationService.removeOfferFromApplications(service);
        userService.removeOffer(service);
        userService.removeOfferFromFavorites(service);
        serviceService.deleteService(serviceId);
    }
}
