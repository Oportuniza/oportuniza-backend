package org.oportuniza.oportunizabackend.offers.repository;

import org.oportuniza.oportunizabackend.offers.model.Job;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long>, JpaSpecificationExecutor<Offer> {
    Page<Offer> findOffersByUserId(long userId, Pageable pageable);
}
