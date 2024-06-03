package org.oportuniza.oportunizabackend.offers.service;

import org.oportuniza.oportunizabackend.offers.dto.JobDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.offers.exceptions.JobNotFoundException;
import org.oportuniza.oportunizabackend.offers.exceptions.ServiceNotFoundException;
import org.oportuniza.oportunizabackend.offers.model.Job;
import org.oportuniza.oportunizabackend.offers.model.Service;
import org.oportuniza.oportunizabackend.offers.repository.OfferRepository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

@org.springframework.stereotype.Service
public class OfferService {
    private OfferRepository offerRepository;

    public List<JobDTO> getAllJobs() {
        List<Job> jobs = offerRepository.findAllJobs();
        return jobs.stream().map(this::convertJobToJobDTO).toList();
    }

    public List<ServiceDTO> getAllServices() {
        List<Service> services = offerRepository.findAllServices();
        return services.stream().map(this::convertServiceToServiceDTO).toList();
    }

    public JobDTO getJob(long jobId) throws JobNotFoundException {
        return convertJobToJobDTO((Job) offerRepository.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId)));
    }

    public ServiceDTO getService(long serviceId) {
        return convertServiceToServiceDTO((Service) offerRepository.findById(serviceId).orElseThrow(() -> new ServiceNotFoundException(serviceId)));
    }

    public JobDTO updateJob(long jobId, JobDTO updatedJob) throws JobNotFoundException {
        Job job = (Job) offerRepository.findById(jobId).orElseThrow(() -> new JobNotFoundException(jobId));
        job.setTitle(updatedJob.title());
        job.setDescription(updatedJob.description());
        job.setNegotiable(updatedJob.negotiable());
        job.setSalary(updatedJob.salary());
        job.setLocalization(updatedJob.localization());
        offerRepository.save(job);
        return updatedJob;
    }

    public ServiceDTO updateService(long serviceId, ServiceDTO updatedService) throws ServiceNotFoundException {
        Service service = (Service) offerRepository.findById(serviceId).orElseThrow(() -> new ServiceNotFoundException(serviceId));
        service.setTitle(updatedService.title());
        service.setDescription(updatedService.description());
        service.setNegotiable(updatedService.negotiable());
        service.setPrice(updatedService.price());
        offerRepository.save(service);
        return updatedService;
    }

    public JobDTO convertJobToJobDTO(Job job) {
        return new JobDTO(job.getId(), job.getTitle(), job.getDescription(), job.isNegotiable(), job.getSalary(), job.getLocalization());
    }

    public ServiceDTO convertServiceToServiceDTO(Service service) {
        return new ServiceDTO(service.getId(), service.getTitle(), service.getDescription(), service.isNegotiable(), service.getPrice());
    }

    public JobDTO createJob(JobDTO job) {
        Job newJob = new Job();
        newJob.setTitle(job.title());
        newJob.setDescription(job.description());
        newJob.setNegotiable(job.negotiable());
        newJob.setSalary(job.salary());
        newJob.setLocalization(job.localization());
        offerRepository.save(newJob);
        return convertJobToJobDTO(newJob);
    }

    public ServiceDTO createService(ServiceDTO service) {
        Service newService = new Service();
        newService.setTitle(service.title());
        newService.setDescription(service.description());
        newService.setNegotiable(service.negotiable());
        newService.setPrice(service.price());
        offerRepository.save(newService);
        return convertServiceToServiceDTO(newService);
    }

    public void deleteJob(long jobId) throws JobNotFoundException{
        try {
            offerRepository.deleteById(jobId);
        } catch (EmptyResultDataAccessException ex) {
            throw new JobNotFoundException(jobId);
        }
    }

    public void deleteService(long serviceId) {
        try {
            offerRepository.deleteById(serviceId);
        } catch (EmptyResultDataAccessException ex) {
            throw new ServiceNotFoundException(serviceId);
        }
    }

    public List<JobDTO> getUserJobs(long userId) {
        List<Job> jobs = offerRepository.findJobsByUserId(userId);
        return jobs.stream().map(this::convertJobToJobDTO).toList();
    }

    public List<ServiceDTO> getUserServices(long userId) {
        List<Service> services = offerRepository.findServicesByUserId(userId);
        return services.stream().map(this::convertServiceToServiceDTO).toList();
    }
}
