package org.oportuniza.oportunizabackend.offers.controller;

import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.offers.dto.CreateServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.offers.model.Service;
import org.oportuniza.oportunizabackend.offers.service.ServiceService;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<ServiceDTO>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<ServiceDTO> getService(@PathVariable long serviceId) {
        return ResponseEntity.ok(serviceService.getService(serviceId));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<ServiceDTO>> getUserServices(@PathVariable long userId) {
        return ResponseEntity.ok(serviceService.getUserServices(userId));
    }

    @PutMapping("/{serviceId}")
    public ResponseEntity<ServiceDTO> updateService(@PathVariable long serviceId, @RequestBody @Valid ServiceDTO updatedService) {
        return ResponseEntity.ok(serviceService.updateService(serviceId, updatedService));
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<ServiceDTO> createService(@PathVariable long userId, @RequestBody @Valid CreateServiceDTO serviceDTO) {
        var user = userService.getUserById(userId);
        var service = serviceService.createService(serviceDTO, user);
        userService.addOffer(userId, service);
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceService.convertServiceToServiceDTO(service));
    }

    @DeleteMapping("/{serviceId}") // remove service from user's offers and users' favorites
    public ResponseEntity<String> deleteService(@PathVariable long serviceId) throws Exception {
        Service service = serviceService.getServiceById(serviceId);
        userService.removeOffer(service);
        userService.removeOfferFromFavorites(service);
        serviceService.deleteService(serviceId);
        return ResponseEntity.ok("Service deleted successfully.");
    }
}
