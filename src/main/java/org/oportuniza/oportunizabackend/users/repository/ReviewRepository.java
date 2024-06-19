package org.oportuniza.oportunizabackend.users.repository;

import org.oportuniza.oportunizabackend.users.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewedId(Long reviewedId);
    Optional<Review> findByReviewerIdAndReviewedId(Long reviewerId, Long reviewedId);
}