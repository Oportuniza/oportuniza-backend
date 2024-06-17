package org.oportuniza.oportunizabackend.offers.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.oportuniza.oportunizabackend.offers.dto.CreateJobDTO;
import org.oportuniza.oportunizabackend.offers.dto.GetJobDTO;
import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.oportuniza.oportunizabackend.offers.dto.UpdateJobDTO;
import org.oportuniza.oportunizabackend.offers.exceptions.JobNotFoundException;
import org.oportuniza.oportunizabackend.offers.model.Job;
import org.oportuniza.oportunizabackend.offers.service.JobService;
import org.oportuniza.oportunizabackend.users.exceptions.UserNotFoundException;
import org.oportuniza.oportunizabackend.users.service.UserService;
import org.oportuniza.oportunizabackend.utils.ErrorResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

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
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all jobs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All jobs obtained", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public Page<JobDTO> getAllJobs(
            @Parameter(description = "The title of the jobs to filter") @RequestParam(required = false) String title,
            @Parameter(description = "The minimum salary of the jobs to filter") @RequestParam(required = false) Double minSalary,
            @Parameter(description = "The maximum salary of the jobs to filter") @RequestParam(required = false) Double maxSalary,
            @Parameter(description = "The working model of the jobs to filter (e.g., remote, on-site)") @RequestParam(required = false) String workingModel,
            @Parameter(description = "The working regime of the jobs to filter (e.g., full-time, part-time)") @RequestParam(required = false) String workingRegime,
            @Parameter(description = "Whether the job salary is negotiable") @RequestParam(required = false) Boolean negotiable,
            @Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page for pagination") @RequestParam(defaultValue = "10") int size) {
        return jobService.getAllJobs(title, minSalary, maxSalary, workingModel, workingRegime, negotiable, page, size);
    }

    @GetMapping("/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get job by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job obtained", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = JobDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Job not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public GetJobDTO getJob(
            @Parameter(description = "The ID of the job to be retrieved") @PathVariable long jobId)
            throws JobNotFoundException, MalformedURLException, URISyntaxException {
        var job = jobService.getJob(jobId);
        return new GetJobDTO(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.isNegotiable(),
                job.getCreatedAt(),
                job.getSalary(),
                job.getLocalization(),
                job.getWorkingModel(),
                job.getWorkingRegime(),
                userService.convertToDTO(job.getUser()));
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get jobs by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User jobs found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            })
    })
    public Page<JobDTO> getUserJobs(
            @Parameter(description = "The ID of the user whose jobs are to be retrieved") @PathVariable long userId,
            @Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page for pagination") @RequestParam(defaultValue = "10") int size) {
        return jobService.getUserJobs(userId, page, size);
    }

    @PutMapping("/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job updated", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = JobDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Job not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public JobDTO updateJob(
            @Parameter(description = "The ID of the job to be updated") @PathVariable long jobId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The updated job details") @RequestPart("job") @Valid UpdateJobDTO updatedJob,
            @RequestPart(value = "image", required = false) MultipartFile image)
            throws JobNotFoundException, IOException, URISyntaxException {
        return jobService.updateJob(jobId, updatedJob, image);
    }

    @PostMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Job created", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = JobDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "User not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public JobDTO createJob(
            @Parameter(description = "The ID of the user creating the job") @PathVariable long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The details of the job to be created") @RequestPart("job") @Valid CreateJobDTO jobDTO,
            @RequestPart(value = "image", required = false) MultipartFile image)
            throws UserNotFoundException, IOException, URISyntaxException {
        var user = userService.getUserById(userId);
        var job = jobService.createJob(jobDTO, user, image);
        userService.addOffer(userId, job);
        return jobService.convertJobToJobDTO(job);
    }

    @DeleteMapping("/{jobId}") // remove job from user's offers and users' favorites
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job deleted", content = {
                    @Content(mediaType = "application/json;charset=UTF-8")
            }),
            @ApiResponse(responseCode = "404", description = "Job not found", content = {
                    @Content(mediaType = "application/json;charset=UTF-8", schema = @Schema(implementation = ErrorResponse.class))
            })
    })
    public void deleteJob(
            @Parameter(description = "The ID of the job to be deleted") @PathVariable long jobId)
            throws JobNotFoundException {
        Job job = jobService.getJobById(jobId);
        userService.removeOffer(job);
        userService.removeOfferFromFavorites(job);
        jobService.deleteJob(jobId);
    }
}
