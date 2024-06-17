package org.oportuniza.oportunizabackend.offers.service;

import org.oportuniza.oportunizabackend.offers.dto.CreateServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.offers.exceptions.ServiceNotFoundException;
import org.oportuniza.oportunizabackend.offers.model.Service;
import org.oportuniza.oportunizabackend.offers.repository.ServiceRepository;
import org.oportuniza.oportunizabackend.offers.service.specifications.OfferSpecifications;
import org.oportuniza.oportunizabackend.offers.service.specifications.ServiceSpecifications;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.service.GoogleCloudStorageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.net.MalformedURLException;
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


    public ServiceDTO updateService(long serviceId, ServiceDTO updatedService) throws ServiceNotFoundException {
        Service service = serviceRepository.findById(serviceId).orElseThrow(() -> new ServiceNotFoundException(serviceId));
        service.setTitle(updatedService.getTitle());
        service.setDescription(updatedService.getDescription());
        service.setNegotiable(updatedService.isNegotiable());
        service.setPrice(updatedService.getPrice());
        serviceRepository.save(service);
        return updatedService;
    }


    public ServiceDTO convertServiceToServiceDTO(Service service) {
        return new ServiceDTO(
                service.getId(),
                service.getTitle(),
                service.getDescription(),
                service.isNegotiable(),
                service.getImageUrl(),
                service.getImageFileName(),
                service.getCreatedAt(),
                service.getPrice());
    }


    public Service createService(CreateServiceDTO service, User user) {
        Service newService = new Service();
        newService.setUser(user);
        newService.setTitle(service.title());
        newService.setDescription(service.description());
        newService.setNegotiable(service.negotiable());
        newService.setPrice(service.price());
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
