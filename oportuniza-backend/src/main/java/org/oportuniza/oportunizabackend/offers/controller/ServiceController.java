package org.oportuniza.oportunizabackend.offers.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.offers.dto.CreateServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.offers.model.Service;
import org.oportuniza.oportunizabackend.offers.service.ServiceService;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/services")
public class ServiceController {
    private final ServiceService serviceService;
    private final UserService userService;

    public ServiceController(ServiceService serviceService, UserService userService) {
        this.serviceService = serviceService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all services")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All services obtained", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public ResponseEntity<Page<ServiceDTO>> getAllServices(
            @RequestParam String title,
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @RequestParam Boolean negotiable,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(serviceService.getAllServices(title, minPrice, maxPrice, negotiable, page, size));
    }

    @GetMapping("/{serviceId}")
    @Operation(summary = "Get service by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service obtained", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ServiceDTO.class))
            })
    })
    public ResponseEntity<ServiceDTO> getService(@PathVariable long serviceId) {
        return ResponseEntity.ok(serviceService.getService(serviceId));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get services by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User services found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public ResponseEntity<Page<ServiceDTO>> getUserServices(@PathVariable long userId,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(serviceService.getUserServices(userId, page, size));
    }

    @PutMapping("/{serviceId}")
    @Operation(summary = "Update service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service updated", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ServiceDTO.class))
            })
    })
    public ResponseEntity<ServiceDTO> updateService(@PathVariable long serviceId, @RequestBody @Valid ServiceDTO updatedService) {
        return ResponseEntity.ok(serviceService.updateService(serviceId, updatedService));
    }

    @PostMapping("/users/{userId}")
    @Operation(summary = "Create service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Service created", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ServiceDTO.class))
            })
    })
    public ResponseEntity<ServiceDTO> createService(@PathVariable long userId, @RequestBody @Valid CreateServiceDTO serviceDTO) {
        var user = userService.getUserById(userId);
        var service = serviceService.createService(serviceDTO, user);
        userService.addOffer(userId, service);
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceService.convertServiceToServiceDTO(service));
    }

    @DeleteMapping("/{serviceId}") // remove service from user's offers and users' favorites
    @Operation(summary = "Delete service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service deleted", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public ResponseEntity<String> deleteService(@PathVariable long serviceId) throws Exception {
        Service service = serviceService.getServiceById(serviceId);
        userService.removeOffer(service);
        userService.removeOfferFromFavorites(service);
        serviceService.deleteService(serviceId);
        return ResponseEntity.ok("Service deleted successfully.");
    }
}
