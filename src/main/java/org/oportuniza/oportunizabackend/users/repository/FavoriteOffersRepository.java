package org.oportuniza.oportunizabackend.users.repository;

import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteOffersRepository extends JpaRepository<Offer, Long> {
    @Query("SELECT u.favoritesOffers FROM User u WHERE u.id = :userId")
    Page<Offer> findFavoriteOffersByUserId(@Param("userId") Long userId, Pageable pageable);
}
