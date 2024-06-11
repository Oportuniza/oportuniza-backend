package org.oportuniza.oportunizabackend.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.offers.dto.OfferDTO;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.offers.service.OfferService;
import org.oportuniza.oportunizabackend.users.dto.RequestDTO;
import org.oportuniza.oportunizabackend.users.dto.UpdateUserDTO;
import org.oportuniza.oportunizabackend.users.dto.UserDTO;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Operation(summary = "Get favorite users from a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite users obtained", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public ResponseEntity<Page<UserDTO>> getFavoriteUsers(@PathVariable long userId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getFavoriteUsers(userId, page, size));
    }

    @GetMapping("/{userId}/favorites/offers")
    @Operation(summary = "Get favorite offers from a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite offers obtained", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public ResponseEntity<Page<OfferDTO>> getFavoriteOffers(@PathVariable long userId,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getFavoriteOffers(userId, page, size));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User obtained", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = UserDTO.class))
            })
    })
    public ResponseEntity<UserDTO> getUserById(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PatchMapping("/{userId}/favorites/add")
    @Operation(summary = "Add favorite user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite user added successfully", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public ResponseEntity<String> addFavoriteUser(@PathVariable long userId, @RequestBody @Valid RequestDTO requestDTO) {
        userService.addFavoriteUser(userId, requestDTO.id());
        return ResponseEntity.ok("Favorite user added successfully.");
    }

    @PatchMapping("/{userId}/favorites/remove")
    @Operation(summary = "Remove favorite user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite user removed successfully", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public ResponseEntity<String> removeFavoriteUser(@PathVariable long userId, @RequestBody @Valid RequestDTO requestDTO) {
        userService.removeFavoriteUser(userId, requestDTO.id());
        return ResponseEntity.ok("Favorite user removed successfully.");
    }

    @PatchMapping("/{userId}/favorites/offers/add")
    @Operation(summary = "Add favorite offer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite offer added successfully", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public ResponseEntity<String> addFavoriteOffer(@PathVariable long userId, @RequestBody @Valid RequestDTO requestDTO) {
        Offer offer = offerService.getOffer(requestDTO.id());
        userService.addFavoriteOffer(userId, offer);
        return ResponseEntity.ok("Favorite offer added successfully.");
    }

    @PatchMapping("/{userId}/favorites/offers/remove")
    @Operation(summary = "Remove favorite offer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite offer removed successfully", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public ResponseEntity<String> removeFavoriteOffer(@PathVariable long userId, @RequestBody @Valid RequestDTO requestDTO) {
        Offer offer = offerService.getOffer(requestDTO.id());
        userService.removeFavoriteOffer(userId, offer);
        return ResponseEntity.ok("Favorite offer removed successfully.");
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = UserDTO.class))
            })
    })
    public ResponseEntity<UserDTO> updateUser(@PathVariable long userId, @RequestBody @Valid UpdateUserDTO updatedUser) {
        return ResponseEntity.ok(userService.updateUser(userId, updatedUser));
    }
}
