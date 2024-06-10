package org.oportuniza.oportunizabackend.offers.service;

import org.oportuniza.oportunizabackend.offers.dto.CreateServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.offers.exceptions.ServiceNotFoundException;
import org.oportuniza.oportunizabackend.offers.model.Service;
import org.oportuniza.oportunizabackend.offers.repository.ServiceRepository;
import org.oportuniza.oportunizabackend.users.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {
    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public Page<ServiceDTO> getAllServices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Service> services = serviceRepository.findAll(pageable);
        return services.map(this::convertServiceToServiceDTO);
    }

    public ServiceDTO getService(long serviceId) {
        return convertServiceToServiceDTO(serviceRepository.findById(serviceId).orElseThrow(() -> new ServiceNotFoundException(serviceId)));
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
        return new ServiceDTO(service.getId(), service.getTitle(), service.getDescription(), service.isNegotiable(), service.getPrice());
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
