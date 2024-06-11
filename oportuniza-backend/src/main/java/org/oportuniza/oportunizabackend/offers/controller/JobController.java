package org.oportuniza.oportunizabackend.offers.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.offers.dto.CreateJobDTO;
import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.oportuniza.oportunizabackend.offers.model.Job;
import org.oportuniza.oportunizabackend.offers.service.JobService;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService jobService;
    private final UserService userService;

    public JobController(JobService jobService, UserService userService) {
        this.jobService = jobService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all jobs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All jobs obtained", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public ResponseEntity<Page<JobDTO>> getAllJobs(
            @RequestParam String title,
            @RequestParam Double minSalary,
            @RequestParam Double maxSalary,
            @RequestParam String workingModel,
            @RequestParam String workingRegime,
            @RequestParam Boolean negotiable,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobService.getAllJobs(title, minSalary, maxSalary, workingModel, workingRegime, negotiable, page, size)); // ADD SUPPORT FOR PAGINATION AND FILTERING
    }

    @GetMapping("/{jobId}")
    @Operation(summary = "Get job by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job obtained", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = JobDTO.class))
            })
    })
    public ResponseEntity<JobDTO> getJob(@PathVariable long jobId) {
        return ResponseEntity.ok(jobService.getJob(jobId));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get jobs by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User jobs found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public ResponseEntity<Page<JobDTO>> getUserJobs(@PathVariable long userId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobService.getUserJobs(userId, page, size));
    }

    @PutMapping("/{jobId}")
    @Operation(summary = "Update job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job updated", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = JobDTO.class))
            })
    })
    public ResponseEntity<JobDTO> updateJob(@PathVariable long jobId, @RequestBody @Valid JobDTO updatedJob) {
        return ResponseEntity.ok(jobService.updateJob(jobId, updatedJob));
    }

    @PostMapping("/users/{userId}")
    @Operation(summary = "Create job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Job created", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = JobDTO.class))
            })
    })
    public ResponseEntity<JobDTO> createJob(@PathVariable long userId, @RequestBody @Valid CreateJobDTO jobDTO) {
        var user = userService.getUserById(userId);
        var job = jobService.createJob(jobDTO, user);
        userService.addOffer(userId, job);
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.convertJobToJobDTO(job));
    }

    @DeleteMapping("/{jobId}") // remove job from user's offers and users' favorites
    @Operation(summary = "Delete job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job deleted", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public ResponseEntity<String> deleteJob(@PathVariable long jobId) throws Exception {
        Job job = jobService.getJobById(jobId);
        userService.removeOffer(job);
        userService.removeOfferFromFavorites(job);
        jobService.deleteJob(jobId);
        return ResponseEntity.ok("Job deleted successfully.");
    }
}
