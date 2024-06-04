package org.oportuniza.oportunizabackend.offers.repository;

import org.oportuniza.oportunizabackend.offers.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findJobsByUserId(long userId);
}
