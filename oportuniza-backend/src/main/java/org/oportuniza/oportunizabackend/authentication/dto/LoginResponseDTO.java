package org.oportuniza.oportunizabackend.authentication.dto;

import java.util.List;

public record LoginResponseDTO(String username, List<String> roles, String jwtToken) {
}
