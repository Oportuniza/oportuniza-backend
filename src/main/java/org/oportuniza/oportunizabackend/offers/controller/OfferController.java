package org.oportuniza.oportunizabackend.offers.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.oportuniza.oportunizabackend.offers.dto.OfferDTO;
import org.oportuniza.oportunizabackend.offers.service.OfferService;
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

}
