package org.oportuniza.oportunizabackend.users.controller;

import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.oportuniza.oportunizabackend.offers.dto.OfferDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.offers.model.Job;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.offers.model.Service;
import org.oportuniza.oportunizabackend.offers.service.JobService;
import org.oportuniza.oportunizabackend.offers.service.OfferService;
import org.oportuniza.oportunizabackend.offers.service.ServiceService;
import org.oportuniza.oportunizabackend.users.dto.RequestDTO;
import org.oportuniza.oportunizabackend.users.dto.UpdateUserDTO;
import org.oportuniza.oportunizabackend.users.dto.UserDTO;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final OfferService offerService;
    private final JobService jobService;
    private final ServiceService serviceService;

    public UserController(final UserService userService, OfferService offerService, JobService jobService, ServiceService serviceService) {
        this.userService = userService;
        this.offerService = offerService;
        this.jobService = jobService;
        this.serviceService = serviceService;
    }

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<UserDTO>> getFavoriteUsers(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getFavoriteUsers(userId));
    }

    @PatchMapping("/{userId}/favorites/add")
    public ResponseEntity<String> addFavoriteUser(@PathVariable long userId, @RequestBody @Valid RequestDTO requestDTO) {
        userService.addFavoriteUser(userId, requestDTO.id());
        return ResponseEntity.ok("Favorite user added successfully.");
    }

    @PatchMapping("/{userId}/favorites/remove")
    public ResponseEntity<String> removeFavoriteUser(@PathVariable long userId, @RequestBody @Valid RequestDTO requestDTO) {
        userService.removeFavoriteUser(userId, requestDTO.id());
        return ResponseEntity.ok("Favorite user removed successfully.");
    }

    @GetMapping("/{userId}/favorites/offers")
    public ResponseEntity<List<OfferDTO>> getFavoriteOffers(@PathVariable long userId) {
        var offers = userService.getFavoriteOffers(userId);
        var offerDTOs = new ArrayList<OfferDTO>();
        for (Offer offer : offers) {
            if (offer instanceof Job job) {
                JobDTO jobDTO = jobService.convertJobToJobDTO(job);
                offerDTOs.add(jobDTO);
            } else if (offer instanceof Service service) {
                ServiceDTO serviceDTO = serviceService.convertServiceToServiceDTO(service);
                offerDTOs.add(serviceDTO);
            }
        }
        return ResponseEntity.ok(offerDTOs);
    }

    @PatchMapping("/{userId}/favorites/offers/add")
    public ResponseEntity<String> addFavoriteOffer(@PathVariable long userId, @RequestBody @Valid RequestDTO requestDTO) {
        Offer offer = offerService.getOffer(requestDTO.id());
        userService.addFavoriteOffer(userId, offer);
        return ResponseEntity.ok("Favorite offer added successfully.");
    }

    @PatchMapping("/{userId}/favorites/offers/remove")
    public ResponseEntity<String> removeFavoriteOffer(@PathVariable long userId, @RequestBody @Valid RequestDTO requestDTO) {
        Offer offer = offerService.getOffer(requestDTO.id());
        userService.removeFavoriteOffer(userId, offer);
        return ResponseEntity.ok("Favorite offer removed successfully.");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable long userId, @RequestBody @Valid UpdateUserDTO updatedUser) {
        return ResponseEntity.ok(userService.updateUser(userId, updatedUser));
    }
}
