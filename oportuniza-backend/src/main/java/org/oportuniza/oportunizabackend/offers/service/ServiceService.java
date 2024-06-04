package org.oportuniza.oportunizabackend.offers.service;

import org.oportuniza.oportunizabackend.offers.dto.CreateServiceDTO;
import org.oportuniza.oportunizabackend.offers.dto.ServiceDTO;
import org.oportuniza.oportunizabackend.offers.exceptions.ServiceNotFoundException;
import org.oportuniza.oportunizabackend.offers.model.Service;
import org.oportuniza.oportunizabackend.offers.repository.ServiceRepository;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {
    private final ServiceRepository serviceRepository;

    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<ServiceDTO> getAllServices() {
        List<Service> services = serviceRepository.findAll();
        return services.stream().map(this::convertServiceToServiceDTO).toList();
    }



    public ServiceDTO getService(long serviceId) {
        return convertServiceToServiceDTO(serviceRepository.findById(serviceId).orElseThrow(() -> new ServiceNotFoundException(serviceId)));
    }


    public ServiceDTO updateService(long serviceId, ServiceDTO updatedService) throws ServiceNotFoundException {
        Service service = serviceRepository.findById(serviceId).orElseThrow(() -> new ServiceNotFoundException(serviceId));
        service.setTitle(updatedService.title());
        service.setDescription(updatedService.description());
        service.setNegotiable(updatedService.negotiable());
        service.setPrice(updatedService.price());
        serviceRepository.save(service);
        return updatedService;
    }


    public ServiceDTO convertServiceToServiceDTO(Service service) {
        return new ServiceDTO(service.getId(), service.getTitle(), service.getDescription(), service.isNegotiable(), service.getPrice());
    }


    public Service createService(CreateServiceDTO service) {
        Service newService = new Service();
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

    public List<ServiceDTO> getUserServices(long userId) {
        List<Service> services = serviceRepository.findServicesByUserId(userId);
        return services.stream().map(this::convertServiceToServiceDTO).toList();
    }
}
