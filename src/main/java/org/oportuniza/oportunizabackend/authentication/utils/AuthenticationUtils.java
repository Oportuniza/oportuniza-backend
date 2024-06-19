package org.oportuniza.oportunizabackend.authentication.utils;

import org.oportuniza.oportunizabackend.authentication.dto.LoginResponseDTO;
import org.oportuniza.oportunizabackend.users.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

public class AuthenticationUtils {

    public static LoginResponseDTO buildLoginResponse(User user) {
        String jwtToken = JwtUtils.generateToken(user);
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new LoginResponseDTO(
                user.getId(),
                user.getEmail(),
                roles,
                user.getName(),
                user.getPhoneNumber(),
                user.getResumeUrl(),
                user.getResumeName(),
                user.getPictureUrl(),
                user.getPictureName(),
                user.getAverageRating(),
                user.getReviewCount(),
                user.getDistrict(),
                user.getCounty(),
                user.getLastActivityAt(),
                user.getCreatedAt(),
                jwtToken);
    }

}
