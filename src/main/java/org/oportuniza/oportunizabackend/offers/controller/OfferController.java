package org.oportuniza.oportunizabackend.offers.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.oportuniza.oportunizabackend.offers.dto.OfferDTO;
import org.oportuniza.oportunizabackend.offers.exceptions.OfferNotFoundException;
import org.oportuniza.oportunizabackend.offers.service.OfferService;
import org.oportuniza.oportunizabackend.users.dto.UserDTO;
import org.oportuniza.oportunizabackend.users.exceptions.UserNotFoundException;
import org.oportuniza.oportunizabackend.utils.ErrorResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offers")
public class OfferController {
    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all offers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All offers obtained", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public Page<OfferDTO> getAllOffers(
            @Parameter(description = "The title of the offers to filter") @RequestParam(required = false) String title,
            @Parameter(description = "Minimum price of the service") @RequestParam(required = false) Double minPrice,
            @Parameter(description = "Maximum price of the service") @RequestParam(required = false) Double maxPrice,
            @Parameter(description = "The minimum salary of the jobs to filter") @RequestParam(required = false) Double minSalary,
            @Parameter(description = "The maximum salary of the jobs to filter") @RequestParam(required = false) Double maxSalary,
            @Parameter(description = "The working model of the jobs to filter (e.g., remote, on-site)") @RequestParam(required = false) String workingModel,
            @Parameter(description = "The working regime of the jobs to filter (e.g., full-time, part-time)") @RequestParam(required = false) String workingRegime,
            @Parameter(description = "Whether the offer salary/price is negotiable") @RequestParam(required = false) Boolean negotiable,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {
        return offerService.getAllOffers(title, minPrice, maxPrice, minSalary, maxSalary, workingModel, workingRegime, negotiable, page, size);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get offers by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User offers found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public Page<OfferDTO> getUserOffers(
            @Parameter(description = "The user id to filter offers") @PathVariable Long userId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {
        return offerService.getOfferByUserId(userId, page, size);
    }

    @PatchMapping("/removeImage/{offerId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Remove image of offer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Offer updated", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = UserDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Offer not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public void removeImage(
            @Parameter(description = "The ID of the offer to be updated") @PathVariable long offerId)
            throws OfferNotFoundException{
        offerService.removeImage(offerId);
    }


    @GetMapping("/{offerId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get offer by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Offer found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            }),
            @ApiResponse(responseCode = "404", description = "Offer not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public OfferDTO getOfferById(
            @Parameter(description = "The ID of the offer to be obtained") @PathVariable long offerId)
            throws OfferNotFoundException {
        return offerService.getOfferById(offerId);
    }

}
