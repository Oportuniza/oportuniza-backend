package org.oportuniza.oportunizabackend.offers.service.specifications;

import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.springframework.data.jpa.domain.Specification;

public class OfferSpecifications {
    public static Specification<Offer> titleContains(String searchString) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + searchString.toLowerCase() + "%");
    }

    public static Specification<Offer> priceGreaterThanOrEqual(double minPrice) {
        return (root, query, cb) -> {
            try {
                return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    public static Specification<Offer> priceLessThanOrEqual(double maxPrice) {
        return (root, query, cb) -> {
            try {
                return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    public static Specification<Offer> salaryGreaterThanOrEqual(double minSalary) {
        return (root, query, cb) -> {
            try {
                return cb.greaterThanOrEqualTo(root.get("salary"), minSalary);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    public static Specification<Offer> salaryLessThanOrEqual(double maxSalary) {
        return (root, query, cb) -> {
            try {
                return cb.lessThanOrEqualTo(root.get("salary"), maxSalary);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    public static Specification<Offer> workingModelEquals(String workingModel) {
        return (root, query, cb) -> {
            try {
                return cb.equal(root.get("workingModel"), workingModel);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    public static Specification<Offer> workingRegimeEquals(String workingRegime) {
        return (root, query, cb) -> {
            try {
                return cb.equal(root.get("workingRegime"), workingRegime);
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    public static Specification<Offer> negotiableEquals(boolean negotiable) {
        return (root, query, cb) -> cb.equal(root.get("negotiable"), negotiable);
    }
}
