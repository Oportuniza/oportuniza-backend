package org.oportuniza.oportunizabackend.applications.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.oportuniza.oportunizabackend.applications.dto.ApplicationDTO;
import org.oportuniza.oportunizabackend.applications.model.Application;

@Mapper
public interface  ApplicationMapper {
    ApplicationMapper INSTANCE = Mappers.getMapper(ApplicationMapper.class);

    ApplicationDTO applicationToApplicationDTO(Application application);

    Application applicationDTOToApplication(ApplicationDTO applicationDTO);
}
