package org.oportuniza.oportunizabackend.users.exceptions;

public class OldPasswordNotProvided extends RuntimeException {
    public OldPasswordNotProvided() {
        super("The old password is not provided.");
    }
}
