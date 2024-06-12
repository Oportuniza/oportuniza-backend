package org.oportuniza.oportunizabackend.applications.exceptions;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(Long id) {
        super("Application with id " + id + " not found.");
    }
}
