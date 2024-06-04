package org.oportuniza.oportunizabackend.offers.repository;

import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

}
