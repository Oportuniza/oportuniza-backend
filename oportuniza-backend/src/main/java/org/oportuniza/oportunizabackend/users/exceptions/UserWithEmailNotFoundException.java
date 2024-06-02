package org.oportuniza.oportunizabackend.users.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserWithEmailNotFoundException extends UsernameNotFoundException {
    public UserWithEmailNotFoundException(String email) {
        super("User with email " + email + " not found");
    }
}