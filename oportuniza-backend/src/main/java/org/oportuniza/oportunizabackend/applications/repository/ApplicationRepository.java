package org.oportuniza.oportunizabackend.applications.repository;

import org.oportuniza.oportunizabackend.applications.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface  ApplicationRepository extends JpaRepository<Application, Long> {
    Page<Application> findByUserId(Long userId, Pageable pageable);
    Page<Application> findByOfferId(Long offerId, Pageable pageable);
}
