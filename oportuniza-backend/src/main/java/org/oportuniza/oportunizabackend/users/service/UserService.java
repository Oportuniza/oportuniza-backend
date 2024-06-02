package org.oportuniza.oportunizabackend.users.service;

import org.oportuniza.oportunizabackend.users.dto.RegisterUserDTO;
import org.oportuniza.oportunizabackend.users.dto.UpdateUserDTO;
import org.oportuniza.oportunizabackend.users.dto.UserDTO;
import org.oportuniza.oportunizabackend.users.exceptions.*;
import org.oportuniza.oportunizabackend.users.model.Role;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO getUserById(Long userId) throws UserWithIdNotFoundException {
        User user = getUser(userId);
        return convertToUserDTO(user);
    }

    public UserDTO updateUser(Long userId, UpdateUserDTO updatedUser)
            throws UserWithIdNotFoundException, OldPasswordNotProvided, NewPasswordNotProvided{
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserWithIdNotFoundException(userId));

        if (updatedUser.name() != null) {
            user.setName(updatedUser.name());
        }
        if (updatedUser.phoneNumber() != null) {
            user.setPhoneNumber(updatedUser.phoneNumber());
        }
        if (updatedUser.resumeUrl() != null) {
            user.setResumeUrl(updatedUser.resumeUrl());
        }
        if (updatedUser.district() != null) {
            user.setDistrict(updatedUser.district());
        }
        if (updatedUser.county() != null) {
            user.setCounty(updatedUser.county());
        }

        updatePasswordIfProvided(user, updatedUser);
        userRepository.save(user);

        return convertToUserDTO(user);
    }

    private void updatePasswordIfProvided(User user, UpdateUserDTO updatedUser)
            throws NewPasswordNotProvided, OldPasswordNotProvided {
        String oldPassword = updatedUser.oldPassword();
        String newPassword = updatedUser.password();

        if (oldPassword != null && newPassword != null) {
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new PasswordMismatchException();
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        } else if (oldPassword != null) {
            throw new NewPasswordNotProvided();
        } else {
            throw new OldPasswordNotProvided();
        }
    }

    public List<UserDTO> getFavoriteUsers(Long userId) throws UserWithIdNotFoundException {
        User user = getUser(userId);
        return user.getFavoriteUsers().stream().map(this::convertToUserDTO).collect(Collectors.toList());
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User createUser(RegisterUserDTO registerUserDTO) {
        String encryptedPassword = passwordEncoder.encode(registerUserDTO.password());
        User user = new User();
        user.setEmail(registerUserDTO.email());
        user.setPassword(encryptedPassword);
        user.addRole(new Role("ROLE_USER"));
        user.setPhoneNumber(registerUserDTO.phoneNumber());
        user.setName(registerUserDTO.name());
        return userRepository.save(user);
    }

    private UserDTO convertToUserDTO(User user) {
        return new UserDTO(
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getDistrict(),
                user.getCounty());
    }

    private User getUser(Long userId) throws UserWithIdNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserWithIdNotFoundException(userId));
    }

}
