package org.oportuniza.oportunizabackend.authentication.dto;

import java.util.List;

public record LoginResponseDTO(String email, List<String> roles, String jwtToken) {
}
