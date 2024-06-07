package org.oportuniza.oportunizabackend.users.repository;

import org.oportuniza.oportunizabackend.users.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByReviewedId(Long reviewedId);
}