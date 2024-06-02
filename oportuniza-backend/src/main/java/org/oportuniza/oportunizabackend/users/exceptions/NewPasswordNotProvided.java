package org.oportuniza.oportunizabackend.users.exceptions;

public class NewPasswordNotProvided extends RuntimeException {
    public NewPasswordNotProvided() {
        super("The new password is not provided.");
    }
}
