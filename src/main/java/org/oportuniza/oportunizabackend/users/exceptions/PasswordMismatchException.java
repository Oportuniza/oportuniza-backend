package org.oportuniza.oportunizabackend.users.exceptions;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super("The old password does not match the current password.");
    }
}