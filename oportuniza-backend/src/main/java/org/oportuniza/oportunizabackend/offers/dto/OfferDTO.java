package org.oportuniza.oportunizabackend.offers.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = JobDTO.class, name = "job"),
        @JsonSubTypes.Type(value = ServiceDTO.class, name = "service")
})
public abstract class OfferDTO {
    private long id;
    private String title;
    private String description;
    private boolean negotiable;

    public OfferDTO(long id, String title, String description, boolean negotiable) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.negotiable = negotiable;
    }

    public OfferDTO() {
    }
}