package org.oportuniza.oportunizabackend.offers.controller;

import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.oportuniza.oportunizabackend.offers.service.OfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final OfferService offerService;

    public JobController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllJobs() {
        return ResponseEntity.ok(offerService.getAllJobs());
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobDTO> getJob(@PathVariable long jobId) {
        return ResponseEntity.ok(offerService.getJob(jobId));
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<JobDTO> updateJob(@PathVariable long jobId, @RequestBody @Valid JobDTO updatedJob) {
        return ResponseEntity.ok(offerService.updateJob(jobId, updatedJob));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<JobDTO>> getUserJobs(@PathVariable long userId) {
        return ResponseEntity.ok(offerService.getUserJobs(userId));
    }

    @PostMapping("/users/{userId}") // change this to insert a job for a specific user
    public ResponseEntity<JobDTO> createJob(@RequestBody @Valid JobDTO job) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offerService.createJob(job));
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<String> deleteJob(@PathVariable long jobId) {
        offerService.deleteJob(jobId);
        return ResponseEntity.ok("Job deleted successfully.");
    }
}
