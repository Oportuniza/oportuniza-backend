package org.oportuniza.oportunizabackend.offers.repository;

import org.oportuniza.oportunizabackend.offers.model.Job;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.offers.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Service> findAllServices();
    List<Job> findAllJobs();
    List<Service> findServicesByUserId(long userId);
    List<Job> findJobsByUserId(long userId);
}
