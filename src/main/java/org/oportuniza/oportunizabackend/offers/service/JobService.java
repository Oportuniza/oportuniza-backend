package org.oportuniza.oportunizabackend.offers.service;

import org.oportuniza.oportunizabackend.offers.dto.CreateJobDTO;
import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.oportuniza.oportunizabackend.offers.dto.UpdateJobDTO;
import org.oportuniza.oportunizabackend.offers.exceptions.JobNotFoundException;
import org.oportuniza.oportunizabackend.offers.model.Job;
import org.oportuniza.oportunizabackend.offers.repository.JobRepository;
import org.oportuniza.oportunizabackend.offers.service.specifications.JobSpecifications;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.service.GoogleCloudStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;


@Service
public class JobService {
    private final JobRepository jobRepository;
    private final GoogleCloudStorageService googleCloudStorageService;

    public JobService(JobRepository jobRepository, GoogleCloudStorageService googleCloudStorageService) {
        this.jobRepository = jobRepository;
        this.googleCloudStorageService = googleCloudStorageService;
    }

    public Page<JobDTO> getAllJobs(String title, Double minSalary, Double maxSalary, String workingModel, String workingRegime, Boolean negotiable, int page, int size) {
        Specification<Job> spec = Specification.where(null);

        if (title != null && !title.isEmpty()) {
            spec = spec.and(JobSpecifications.titleContains(title));
        }
        if (minSalary != null) {
            spec = spec.and(JobSpecifications.minSalaryGreaterThanOrEqual(minSalary));
        }
        if (maxSalary != null) {
            spec = spec.and(JobSpecifications.maxSalaryLessThanOrEqual(maxSalary));
        }
        if (workingModel != null && !workingModel.isEmpty()) {
            spec = spec.and(JobSpecifications.workingModelEquals(workingModel));
        }
        if (workingRegime != null && !workingRegime.isEmpty()) {
            spec = spec.and(JobSpecifications.workingRegimeEquals(workingRegime));
        }
        if (negotiable != null) {
            spec = spec.and(JobSpecifications.negotiableEquals(negotiable));
        }

        Page<Job> jobs = jobRepository.findAll(spec, PageRequest.of(page, size));
        return jobs.map(this::convertJobToJobDTO);
    }

    public JobDTO convertJobToJobDTO(Job job) {
        return new JobDTO(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.isNegotiable(),
                job.getImageUrl(),
                job.getImageFileName(),
                job.getCreatedAt(),
                job.getSalary(),
                job.getDistrict(),
                job.getCounty(),
                job.getWorkingModel(),
                job.getWorkingRegime());
    }

    public Job getJob(long jobId) throws JobNotFoundException {
        return jobRepository.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
    }

    public JobDTO updateJob(long jobId, UpdateJobDTO updatedJob, MultipartFile image) throws JobNotFoundException, IOException, URISyntaxException {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
        if (updatedJob.title() != null && !updatedJob.title().isEmpty()) {
            job.setTitle(updatedJob.title());
        }
        if (updatedJob.description() != null && !updatedJob.description().isEmpty()) {
            job.setDescription(updatedJob.description());
        }
        if (updatedJob.negotiable() != null) {
            job.setNegotiable(updatedJob.negotiable());
        }
        if (updatedJob.salary() != null) {
            job.setSalary(updatedJob.salary());
        }
        if (updatedJob.district() != null && !updatedJob.district().isEmpty()) {
            job.setDistrict(updatedJob.district());
        }
        if (updatedJob.county() != null && !updatedJob.county().isEmpty()) {
            job.setCounty(updatedJob.county());
        }
        if (updatedJob.workingModel() != null && !updatedJob.workingModel().isEmpty()) {
            job.setWorkingModel(updatedJob.workingModel());
        }
        if (updatedJob.workingRegime() != null && !updatedJob.workingRegime().isEmpty()) {
            job.setWorkingRegime(updatedJob.workingRegime());
        }
        if (image != null && !image.isEmpty()) {
            if (job.getImageUrl() != null && job.getImageFileName()!= null && !job.getImageFileName().isEmpty()) {
                googleCloudStorageService.deleteFile(job.getImageFileName());
            }
            var imageUrl = googleCloudStorageService.uploadFile(image);
            job.setImageUrl(imageUrl.getValue1());
            job.setImageFileName(imageUrl.getValue0());
        }
        jobRepository.save(job);
        return convertJobToJobDTO(job);
    }

    public Job createJob(CreateJobDTO job, User user, MultipartFile image) throws IOException, URISyntaxException {
        Job newJob = new Job();
        newJob.setUser(user);
        newJob.setTitle(job.title());
        newJob.setDescription(job.description());
        newJob.setNegotiable(job.negotiable());
        newJob.setSalary(job.salary());
        newJob.setDistrict(job.district());
        newJob.setCounty(job.county());
        newJob.setWorkingModel(job.workingModel());
        newJob.setWorkingRegime(job.workingRegime());
        if (image != null && !image.isEmpty()) {
            var imageUrl = googleCloudStorageService.uploadFile(image);
            newJob.setImageUrl(imageUrl.getValue1());
            newJob.setImageFileName(imageUrl.getValue0());
        }
        jobRepository.save(newJob);
        return newJob;
    }

    public void deleteJob(long jobId) throws JobNotFoundException {
        Job job = getJob(jobId);
        if (job.getImageFileName() != null && !job.getImageFileName().isEmpty()) {
            googleCloudStorageService.deleteFile(job.getImageFileName());
        }
        jobRepository.deleteById(jobId);
    }

    public Job getJobById(long jobId) throws JobNotFoundException {
        return jobRepository.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
    }

    public Page<JobDTO> getUserJobs(long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobs = jobRepository.findJobsByUserId(userId, pageable);
        return jobs.map(this::convertJobToJobDTO);
    }

}
