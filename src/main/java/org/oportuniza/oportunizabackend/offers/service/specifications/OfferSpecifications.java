package org.oportuniza.oportunizabackend.offers.service.specifications;

import org.oportuniza.oportunizabackend.offers.model.Job;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.offers.model.Service;
import org.springframework.data.jpa.domain.Specification;

public class OfferSpecifications {
    public static Specification<Offer> titleContains(String searchString) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + searchString.toLowerCase() + "%");
    }

    public static Specification<Offer> priceGreaterThanOrEqual(double minPrice) {
        return (root, query, cb) -> {
            if (root.getJavaType().equals(Service.class)) {
                return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            }
            return null;
        };
    }

    public static Specification<Offer> priceLessThanOrEqual(double maxPrice) {
        return (root, query, cb) -> {
            if (root.getJavaType().equals(Service.class)) {
                return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
            return null;
        };
    }

    public static Specification<Offer> salaryGreaterThanOrEqual(double minSalary) {
        return (root, query, cb) -> {
            if (root.getJavaType().equals(Job.class)) {
                return cb.greaterThanOrEqualTo(root.get("salary"), minSalary);
            }
            return null;
        };
    }

    public static Specification<Offer> salaryLessThanOrEqual(double maxSalary) {
        return (root, query, cb) -> {
            if (root.getJavaType().equals(Job.class)) {
                return cb.lessThanOrEqualTo(root.get("salary"), maxSalary);
            }
            return null;
        };
    }

    public static Specification<Offer> workingModelEquals(String workingModel) {
        return (root, query, cb) -> {
            if (root.getJavaType().equals(Job.class)) {
                return cb.equal(root.get("workingModel"), workingModel);
            }
            return null;
        };
    }

    public static Specification<Offer> workingRegimeEquals(String workingRegime) {
        return (root, query, cb) -> {
            if (root.getJavaType().equals(Job.class)) {
                return cb.equal(root.get("workingRegime"), workingRegime);
            }
            return null;
        };
    }

    public static Specification<Offer> negotiableEquals(boolean negotiable) {
        return (root, query, cb) -> cb.equal(root.get("negotiable"), negotiable);
    }
}
