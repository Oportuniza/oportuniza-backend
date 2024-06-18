package org.oportuniza.oportunizabackend.offers.service;

import org.oportuniza.oportunizabackend.offers.dto.CreateServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.UpdateServiceDTO;
import org.oportuniza.oportunizabackend.offers.exceptions.ServiceNotFoundException;
import org.oportuniza.oportunizabackend.offers.model.Service;
import org.oportuniza.oportunizabackend.offers.repository.ServiceRepository;
import org.oportuniza.oportunizabackend.offers.service.specifications.ServiceSpecifications;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.service.GoogleCloudStorageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;


@org.springframework.stereotype.Service
public class ServiceService {
    private final ServiceRepository serviceRepository;
    private final GoogleCloudStorageService googleCloudStorageService;

    public ServiceService(ServiceRepository serviceRepository, GoogleCloudStorageService googleCloudStorageService) {
        this.serviceRepository = serviceRepository;
        this.googleCloudStorageService = googleCloudStorageService;
    }

    public Page<ServiceDTO> getAllServices(String title, Double minPrice, Double maxPrice, Boolean negotiable, int page, int size) {
        Specification<Service> spec = Specification.where(null);

        if (title != null && !title.isEmpty()) {
            spec = spec.and(ServiceSpecifications.titleContains(title));
        }
        if (minPrice != null) {
            spec = spec.and(ServiceSpecifications.priceGreaterThanOrEqual(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(ServiceSpecifications.priceLessThanOrEqual(maxPrice));
        }
        if (negotiable != null) {
            spec = spec.and(ServiceSpecifications.negotiableEquals(negotiable));;
        }

        Page<Service> services = serviceRepository.findAll(spec, PageRequest.of(page, size));
        return services.map(this::convertServiceToServiceDTO);
    }

    public Service getService(long serviceId) throws ServiceNotFoundException {
        return serviceRepository.findById(serviceId).orElseThrow(() -> new ServiceNotFoundException(serviceId));
    }


    public ServiceDTO updateService(long serviceId, UpdateServiceDTO updatedService, MultipartFile image) throws ServiceNotFoundException, IOException, URISyntaxException {
        Service service = serviceRepository.findById(serviceId).orElseThrow(() -> new ServiceNotFoundException(serviceId));
        if (updatedService.negotiable() != null) {
            service.setNegotiable(updatedService.negotiable());
        }
        if (updatedService.price() != null) {
            service.setPrice(updatedService.price());
        }
        if (updatedService.title() != null && !updatedService.title().isEmpty()) {
            service.setTitle(updatedService.title());
        }
        if (updatedService.description() != null && !updatedService.description().isEmpty()) {
            service.setDescription(updatedService.description());
        }
        if (updatedService.district() != null && !updatedService.district().isEmpty()) {
            service.setDistrict(updatedService.district());
        }
        if (updatedService.county() != null && !updatedService.county().isEmpty()) {
            service.setCounty(updatedService.county());
        }
        if (image != null && !image.isEmpty()) {
            if (service.getImageUrl() != null && service.getImageFileName()!= null && !service.getImageFileName().isEmpty()) {
                googleCloudStorageService.deleteFile(service.getImageFileName());
            }
            var imageUrl = googleCloudStorageService.uploadFile(image);
            service.setImageUrl(imageUrl.getValue1());
            service.setImageFileName(imageUrl.getValue0());
        }
        serviceRepository.save(service);
        return convertServiceToServiceDTO(service);
    }


    public ServiceDTO convertServiceToServiceDTO(Service service) {
        return new ServiceDTO(
                service.getId(),
                service.getTitle(),
                service.getDescription(),
                service.getDistrict(),
                service.getCounty(),
                service.isNegotiable(),
                service.getImageUrl(),
                service.getImageFileName(),
                service.getCreatedAt(),
                service.getPrice());
    }


    public Service createService(CreateServiceDTO service, User user, MultipartFile image) throws IOException, URISyntaxException {
        Service newService = new Service();
        newService.setUser(user);
        newService.setTitle(service.title());
        newService.setDescription(service.description());
        newService.setNegotiable(service.negotiable());
        newService.setPrice(service.price());
        newService.setDistrict(service.district());
        newService.setCounty(service.county());
        if (image != null && !image.isEmpty()) {
            var imageUrl = googleCloudStorageService.uploadFile(image);
            newService.setImageUrl(imageUrl.getValue1());
            newService.setImageFileName(imageUrl.getValue0());
        }
        serviceRepository.save(newService);
        return newService;
    }

    public Service getServiceById(long serviceId) throws ServiceNotFoundException {
        return serviceRepository.findById(serviceId).orElseThrow(() -> new ServiceNotFoundException(serviceId));
    }

    public void deleteService(long serviceId) {
        serviceRepository.deleteById(serviceId);
    }

    public Page<ServiceDTO> getUserServices(long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Service> services = serviceRepository.findServicesByUserId(userId, pageable);
        return services.map(this::convertServiceToServiceDTO);
    }
}
