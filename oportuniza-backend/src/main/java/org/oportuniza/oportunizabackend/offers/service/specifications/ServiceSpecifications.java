package org.oportuniza.oportunizabackend.offers.service.specifications;

import org.oportuniza.oportunizabackend.offers.model.Service;
import org.springframework.data.jpa.domain.Specification;

public class ServiceSpecifications {

    public static Specification<Service> titleContains(String searchString) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + searchString.toLowerCase() + "%");
    }

    public static Specification<Service> priceGreaterThanOrEqual(double minPrice) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Service> priceLessThanOrEqual(double maxPrice) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Service> negotiableEquals(boolean negotiable) {
        return (root, query, cb) -> cb.equal(root.get("negotiable"), negotiable);
    }
}
