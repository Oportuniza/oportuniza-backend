package org.oportuniza.oportunizabackend.offers.service;

import org.oportuniza.oportunizabackend.offers.dto.CreateJobDTO;
import org.oportuniza.oportunizabackend.offers.dto.GetJobDTO;
import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.oportuniza.oportunizabackend.offers.exceptions.JobNotFoundException;
import org.oportuniza.oportunizabackend.offers.model.Job;
import org.oportuniza.oportunizabackend.offers.repository.JobRepository;
import org.oportuniza.oportunizabackend.offers.service.specifications.JobSpecifications;
import org.oportuniza.oportunizabackend.users.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
public class JobService {
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
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
                job.getCreatedAt(),
                job.getSalary(),
                job.getLocalization(),
                job.getWorkingModel(),
                job.getWorkingRegime());
    }

    public Job getJob(long jobId) throws JobNotFoundException {
        return jobRepository.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
    }

    public JobDTO updateJob(long jobId, JobDTO updatedJob) throws JobNotFoundException {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
        job.setTitle(updatedJob.getTitle());
        job.setDescription(updatedJob.getDescription());
        job.setNegotiable(updatedJob.isNegotiable());
        job.setSalary(updatedJob.getSalary());
        job.setLocalization(updatedJob.getLocalization());
        job.setWorkingModel(updatedJob.getWorkingModel());
        job.setWorkingRegime(updatedJob.getWorkingRegime());
        jobRepository.save(job);
        return updatedJob;
    }

    public Job createJob(CreateJobDTO job, User user) {
        Job newJob = new Job();
        newJob.setUser(user);
        newJob.setTitle(job.title());
        newJob.setDescription(job.description());
        newJob.setNegotiable(job.negotiable());
        newJob.setSalary(job.salary());
        newJob.setLocalization(job.localization());
        newJob.setWorkingModel(job.workingModel());
        newJob.setWorkingRegime(job.workingRegime());
        jobRepository.save(newJob);
        return newJob;
    }

    public void deleteJob(long jobId) {
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
