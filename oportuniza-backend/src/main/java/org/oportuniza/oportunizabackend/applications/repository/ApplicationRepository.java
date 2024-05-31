package org.oportuniza.oportunizabackend.applications.repository;

import org.oportuniza.oportunizabackend.applications.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUserId(Long userId);
    List<Application> findByOfferId(Long offerId);
}
