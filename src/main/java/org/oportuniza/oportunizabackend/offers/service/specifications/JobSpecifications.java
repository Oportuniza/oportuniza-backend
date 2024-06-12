package org.oportuniza.oportunizabackend.offers.service.specifications;

import org.oportuniza.oportunizabackend.offers.model.Job;
import org.springframework.data.jpa.domain.Specification;

public class JobSpecifications {

    public static Specification<Job> titleContains(String searchString) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + searchString.toLowerCase() + "%");
    }

    public static Specification<Job> minSalaryGreaterThanOrEqual(double minSalary) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("salary"), minSalary);
    }

    public static Specification<Job> maxSalaryLessThanOrEqual(double maxSalary) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("salary"), maxSalary);
    }

    public static Specification<Job> workingModelEquals(String workingModel) {
        if (workingModel == null || workingModel.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("workingModel"), workingModel);
    }

    public static Specification<Job> workingRegimeEquals(String workingRegime) {
        if (workingRegime == null || workingRegime.isEmpty()) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("workingRegime"), workingRegime);
    }

    public static Specification<Job> negotiableEquals(boolean negotiable) {
        return (root, query, cb) -> cb.equal(root.get("negotiable"), negotiable);
    }
}