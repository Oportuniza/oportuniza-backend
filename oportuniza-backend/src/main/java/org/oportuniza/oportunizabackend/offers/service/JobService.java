package org.oportuniza.oportunizabackend.offers.service;

import org.oportuniza.oportunizabackend.offers.dto.CreateJobDTO;
import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.oportuniza.oportunizabackend.offers.exceptions.JobNotFoundException;
import org.oportuniza.oportunizabackend.offers.model.Job;
import org.oportuniza.oportunizabackend.offers.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<JobDTO> getAllJobs() {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream().map(this::convertJobToJobDTO).toList();
    }

    public JobDTO convertJobToJobDTO(Job job) {
        return new JobDTO(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.isNegotiable(),
                job.getSalary(),
                job.getLocalization(),
                job.getWorkingModel(),
                job.getWorkingRegime());
    }

    public JobDTO getJob(long jobId) throws JobNotFoundException {
        return convertJobToJobDTO(jobRepository.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId)));
    }

    public JobDTO updateJob(long jobId, JobDTO updatedJob) throws JobNotFoundException {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
        job.setTitle(updatedJob.title());
        job.setDescription(updatedJob.description());
        job.setNegotiable(updatedJob.negotiable());
        job.setSalary(updatedJob.salary());
        job.setLocalization(updatedJob.localization());
        job.setWorkingModel(updatedJob.workingModel());
        job.setWorkingRegime(updatedJob.workingRegime());
        jobRepository.save(job);
        return updatedJob;
    }

    public Job createJob(CreateJobDTO job) {
        Job newJob = new Job();
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

    public List<JobDTO> getUserJobs(long userId) {
        List<Job> jobs = jobRepository.findJobsByUserId(userId);
        return jobs.stream().map(this::convertJobToJobDTO).toList();
    }

}
