package org.oportuniza.oportunizabackend.users.controller;

import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.offers.service.OfferService;
import org.oportuniza.oportunizabackend.users.dto.UpdateUserDTO;
import org.oportuniza.oportunizabackend.users.dto.UserDTO;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final OfferService offerService;

    public UserController(final UserService userService, OfferService offerService) {
        this.userService = userService;
        this.offerService = offerService;
    }

    @GetMapping("/{userEmail}/favorites")
    public ResponseEntity<List<UserDTO>> getFavoriteUsers(@PathVariable String userEmail) {
        return ResponseEntity.ok(userService.getFavoriteUsers(userEmail));
    }

    @PatchMapping("/{userEmail}/favorites/add")
    public ResponseEntity<String> addFavoriteUser(@PathVariable String userEmail, @RequestBody @Valid long id) {
        userService.addFavoriteUser(userEmail, id);
        return ResponseEntity.ok("Favorite user added successfully.");
    }

    @PatchMapping("/{userEmail}/favorites/remove")
    public ResponseEntity<String> removeFavoriteUser(@PathVariable String userEmail, @RequestBody @Valid long id) {
        userService.removeFavoriteUser(userEmail, id);
        return ResponseEntity.ok("Favorite user removed successfully.");
    }

    @PatchMapping("/{userEmail}/favorites/offers/add")
    public ResponseEntity<String> addFavoriteOffer(@PathVariable String userEmail, @RequestBody @Valid long offerId) {
        Offer offer = offerService.getOffer(offerId);
        userService.addFavoriteOffer(userEmail, offer);
        return ResponseEntity.ok("Favorite offer added successfully.");
    }

    @PatchMapping("/{userEmail}/favorites/offers/remove")
    public ResponseEntity<String> removeFavoriteOffer(@PathVariable String userEmail, @RequestBody @Valid long offerId) {
        Offer offer = offerService.getOffer(offerId);
        userService.removeFavoriteOffer(userEmail, offer);
        return ResponseEntity.ok("Favorite offer removed successfully.");
    }

    @GetMapping("/{userEmail}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String userEmail) {
        return ResponseEntity.ok(userService.getUser(userEmail));
    }

    @PutMapping("/{userEmail}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String userEmail, @RequestBody @Valid UpdateUserDTO updatedUser) {
        return ResponseEntity.ok(userService.updateUser(userEmail, updatedUser));
    }
}
