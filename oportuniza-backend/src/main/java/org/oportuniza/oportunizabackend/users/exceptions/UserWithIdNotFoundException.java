package org.oportuniza.oportunizabackend.users.exceptions;

public class UserWithIdNotFoundException extends RuntimeException {
    public UserWithIdNotFoundException(Long id) {
        super("User with ID " + id + " not found");
    }
}

