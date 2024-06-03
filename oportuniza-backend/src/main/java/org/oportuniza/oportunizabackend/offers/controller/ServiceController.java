package org.oportuniza.oportunizabackend.offers.controller;

import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.offers.service.OfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    private final OfferService offerService;

    public ServiceController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceDTO>> getAllServices() {
        return ResponseEntity.ok(offerService.getAllServices());
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<ServiceDTO> getService(@PathVariable long serviceId) {
        return ResponseEntity.ok(offerService.getService(serviceId));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<ServiceDTO>> getUserServices(@PathVariable long userId) {
        return ResponseEntity.ok(offerService.getUserServices(userId));
    }

    @PutMapping("/{serviceId}")
    public ResponseEntity<ServiceDTO> updateService(@PathVariable long serviceId, @RequestBody @Valid ServiceDTO updatedService) {
        return ResponseEntity.ok(offerService.updateService(serviceId, updatedService));
    }

    @PostMapping("/users/{userId}")  // change this to insert a service for a specific user
    public ResponseEntity<ServiceDTO> createService(@RequestBody @Valid ServiceDTO service) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offerService.createService(service));
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<String> deleteService(@PathVariable long serviceId) {
        offerService.deleteService(serviceId);
        return ResponseEntity.ok("Service deleted successfully.");
    }
}
