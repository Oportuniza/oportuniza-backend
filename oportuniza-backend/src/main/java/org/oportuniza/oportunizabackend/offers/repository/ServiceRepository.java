package org.oportuniza.oportunizabackend.offers.repository;

import org.oportuniza.oportunizabackend.offers.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository  extends JpaRepository<Service, Long> {
    List<Service> findServicesByUserId(long userId);
}
