package org.oportuniza.oportunizabackend.offers.service.specifications;

import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.springframework.data.jpa.domain.Specification;

public class OfferSpecifications {
    public static Specification<Offer> titleContains(String searchString) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + searchString.toLowerCase() + "%");
    }
}
