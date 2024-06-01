package org.oportuniza.oportunizabackend.users.dto;

import lombok.Data;
import org.oportuniza.oportunizabackend.users.model.Role;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String password;
    private Role role;
}