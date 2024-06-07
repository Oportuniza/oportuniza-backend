package org.oportuniza.oportunizabackend.users.controller;


import org.oportuniza.oportunizabackend.users.dto.CreateReviewDTO;
import org.oportuniza.oportunizabackend.users.dto.ReviewDTO;
import org.oportuniza.oportunizabackend.users.service.ReviewService;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody CreateReviewDTO createReviewDTO) {
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
