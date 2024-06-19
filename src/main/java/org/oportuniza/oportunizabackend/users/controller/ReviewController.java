package org.oportuniza.oportunizabackend.users.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.oportuniza.oportunizabackend.users.dto.CreateReviewDTO;
import org.oportuniza.oportunizabackend.users.dto.ReviewDTO;
import org.oportuniza.oportunizabackend.users.exceptions.UserNotFoundException;
import org.oportuniza.oportunizabackend.users.service.ReviewService;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.oportuniza.oportunizabackend.utils.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @GetMapping("/{reviewerId}/{reviewedId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ReviewDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Review not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public ReviewDTO getReview(@PathVariable long reviewerId, @PathVariable long reviewedId) {
        return reviewService.getReview(reviewerId, reviewedId);
    }

    @PostMapping
    @Operation(summary = "Create review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ReviewDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public ResponseEntity<ReviewDTO> createReview(@RequestBody CreateReviewDTO createReviewDTO) throws UserNotFoundException {
        var reviewer = userService.getUserById(createReviewDTO.reviewerId());
        var reviewed = userService.getUserById(createReviewDTO.reviewedId());
        var reviewDTO = reviewService.createReview(createReviewDTO.rating(), reviewer, reviewed);
        var averageRating = reviewService.getUserAverageRating(createReviewDTO.reviewedId());
        if (averageRating.isPresent()) {
            userService.updateUserRating(createReviewDTO.reviewedId(), averageRating.getAsDouble());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewDTO);
    }
}
