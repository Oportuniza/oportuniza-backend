package org.oportuniza.oportunizabackend.users.service;

import org.oportuniza.oportunizabackend.users.dto.ReviewDTO;
import org.oportuniza.oportunizabackend.users.model.Review;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.OptionalDouble;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public ReviewDTO createReview(int rating, User reviewer, User reviewed) {
        var review = new Review();
        review.setReviewed(reviewed);
        review.setReviewer(reviewer);
        review.setRating(rating);
        reviewRepository.save(review);
        return convertReviewToReviewDTO(review);
    }

    private ReviewDTO convertReviewToReviewDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getReviewer().getId(),
                review.getReviewed().getId(),
                review.getRating()
        );
    }

    public OptionalDouble getUserAverageRating(long userId) {
        List<Review> reviews = reviewRepository.findByReviewedId(userId);
        return reviews.stream().mapToInt(Review::getRating).average();
    }
}