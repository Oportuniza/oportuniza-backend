package org.oportuniza.oportunizabackend.users.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.offers.dto.OfferDTO;
import org.oportuniza.oportunizabackend.offers.exceptions.OfferNotFoundException;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.offers.service.OfferService;
import org.oportuniza.oportunizabackend.users.dto.RequestDTO;
import org.oportuniza.oportunizabackend.users.dto.UpdateUserDTO;
import org.oportuniza.oportunizabackend.users.dto.UserDTO;
import org.oportuniza.oportunizabackend.users.exceptions.NewPasswordNotProvided;
import org.oportuniza.oportunizabackend.users.exceptions.OldPasswordNotProvided;
import org.oportuniza.oportunizabackend.users.exceptions.PasswordMismatchException;
import org.oportuniza.oportunizabackend.users.exceptions.UserNotFoundException;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.oportuniza.oportunizabackend.utils.ErrorResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get favorite users from a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite users obtained", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public Page<UserDTO> getFavoriteUsers(
            @Parameter(description = "The ID of the user whose favorite users are to be retrieved") @PathVariable long userId,
            @Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page for pagination") @RequestParam(defaultValue = "10") int size) {
        return userService.getFavoriteUsers(userId, page, size);
    }

    @GetMapping("/{userId}/favorites/offers")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get favorite offers from a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite offers obtained", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public Page<OfferDTO> getFavoriteOffers(
            @Parameter(description = "The ID of the user whose favorite offers are to be retrieved") @PathVariable long userId,
            @Parameter(description = "Page number for pagination. Default is 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page for pagination. Default is 10") @RequestParam(defaultValue = "10") int size) {
        return userService.getFavoriteOffers(userId, page, size);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User obtained", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = UserDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public UserDTO getUserById(
            @Parameter(description = "The ID of the user to be retrieved") @PathVariable long userId)
            throws UserNotFoundException {
        return userService.getUser(userId);
    }

    @PatchMapping("/{userId}/favorites/add")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Add favorite user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite user added successfully", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public void addFavoriteUser(
            @Parameter(description = "The ID of the user who wants to add a favorite user") @PathVariable long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The ID of the user to be added as a favorite") @RequestBody @Valid RequestDTO requestDTO)
            throws UserNotFoundException {
        userService.addFavoriteUser(userId, requestDTO.id());
    }

    @PatchMapping("/{userId}/favorites/remove")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Remove favorite user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite user removed successfully", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public void removeFavoriteUser(
            @Parameter(description = "The ID of the user who wants to remove a favorite user") @PathVariable long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The ID of the user to be removed from the favorites") @RequestBody @Valid RequestDTO requestDTO)
            throws UserNotFoundException {
        userService.removeFavoriteUser(userId, requestDTO.id());
    }

    @PatchMapping("/{userId}/favorites/offers/add")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Add favorite offer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite offer added successfully", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            }),
            @ApiResponse(responseCode = "404", description = "User or offer not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public void addFavoriteOffer(
            @Parameter(description = "The ID of the user who wants to add a favorite offer") @PathVariable long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The ID of the offer to be add to the favorites") @RequestBody @Valid RequestDTO requestDTO)
            throws UserNotFoundException, OfferNotFoundException {
        Offer offer = offerService.getOffer(requestDTO.id());
        userService.addFavoriteOffer(userId, offer);
    }

    @PatchMapping("/{userId}/favorites/offers/remove")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Remove favorite offer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite offer removed successfully", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            }),
            @ApiResponse(responseCode = "404", description = "User or offer not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public void removeFavoriteOffer(
            @Parameter(description = "The ID of the user who wants to remove a favorite offer") @PathVariable long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The ID of the offer to be removed from the favorites") @RequestBody @Valid RequestDTO requestDTO)
            throws OfferNotFoundException, UserNotFoundException {
        Offer offer = offerService.getOffer(requestDTO.id());
        userService.removeFavoriteOffer(userId, offer);
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = UserDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters or data", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public UserDTO updateUser(
            @Parameter(description = "The ID of the user to be updated") @PathVariable long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The new details for updating the user") @RequestPart("user") @Valid UpdateUserDTO updatedUser,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "resumeFile", required = false) MultipartFile resumeFile)
            throws UserNotFoundException, OldPasswordNotProvided, NewPasswordNotProvided, PasswordMismatchException, IOException {
        return userService.updateUser(userId, updatedUser, profileImage, resumeFile);
    }
}
