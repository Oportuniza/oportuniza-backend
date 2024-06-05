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

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<UserDTO>> getFavoriteUsers(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getFavoriteUsers(userId));
    }

    @PatchMapping("/{userId}/favorites/add")
    public ResponseEntity<String> addFavoriteUser(@PathVariable long userId, @RequestBody @Valid long id) {
        userService.addFavoriteUser(userId, id);
        return ResponseEntity.ok("Favorite user added successfully.");
    }

    @PatchMapping("/{userId}/favorites/remove")
    public ResponseEntity<String> removeFavoriteUser(@PathVariable long userId, @RequestBody @Valid long id) {
        userService.removeFavoriteUser(userId, id);
        return ResponseEntity.ok("Favorite user removed successfully.");
    }

    @PatchMapping("/{userId}/favorites/offers/add")
    public ResponseEntity<String> addFavoriteOffer(@PathVariable long userId, @RequestBody @Valid long offerId) {
        Offer offer = offerService.getOffer(offerId);
        userService.addFavoriteOffer(userId, offer);
        return ResponseEntity.ok("Favorite offer added successfully.");
    }

    @PatchMapping("/{userId}/favorites/offers/remove")
    public ResponseEntity<String> removeFavoriteOffer(@PathVariable long userId, @RequestBody @Valid long offerId) {
        Offer offer = offerService.getOffer(offerId);
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
