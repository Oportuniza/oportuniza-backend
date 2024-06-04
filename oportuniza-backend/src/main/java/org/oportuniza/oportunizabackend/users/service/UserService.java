package org.oportuniza.oportunizabackend.users.service;

import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.users.dto.UpdateUserDTO;
import org.oportuniza.oportunizabackend.users.dto.UserDTO;
import org.oportuniza.oportunizabackend.users.exceptions.*;
import org.oportuniza.oportunizabackend.users.model.Role;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.repository.RoleRepository;
import org.oportuniza.oportunizabackend.users.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(final UserRepository userRepository, RoleRepository roleRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO getUser(String userEmail) throws UserWithEmailNotFoundException {
        User user = getUserByEmail(userEmail);
        return convertToUserDTO(user);
    }

    public UserDTO updateUser(String userEmail, UpdateUserDTO updatedUser)
            throws UserWithIdNotFoundException, OldPasswordNotProvided, NewPasswordNotProvided{
        User user = getUserByEmail(userEmail);

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

    public List<UserDTO> getFavoriteUsers(String userEmail) throws UserWithIdNotFoundException {
        User user = getUserByEmail(userEmail);
        return user.getFavoriteUsers().stream().map(this::convertToUserDTO).toList();
    }

    public void addFavoriteUser(String userEmail, long id) throws UserWithIdNotFoundException, UserWithEmailNotFoundException {
        User user = getUserByEmail(userEmail);
        User favoriteUser = getUserById(id);
        user.addFavoriteUser(favoriteUser);
        userRepository.save(user);
    }

    public void removeFavoriteUser(String userEmail, long id) throws UserWithIdNotFoundException, UserWithEmailNotFoundException {
        User user = getUserByEmail(userEmail);
        User favoriteUser = getUserById(id);
        user.removeFavoriteUser(favoriteUser);
        userRepository.save(user);
    }

    public void addOffer(long id, Offer offer) throws UserWithIdNotFoundException {
        User user = getUserById(id);
        user.addOffer(offer);
        userRepository.save(user);
    }

    public void removeOffer(Offer offer) {
        var user = offer.getUser();
        user.removeOffer(offer);
        userRepository.save(user);
    }

    public void addFavoriteOffer(String userEmail, Offer offer) throws UserWithEmailNotFoundException {
        User user = getUserByEmail(userEmail);
        user.addFavoriteOffer(offer);
        userRepository.save(user);
    }

    public void removeFavoriteOffer(String userEmail, Offer offer) throws UserWithIdNotFoundException, UserWithEmailNotFoundException {
        User user = getUserByEmail(userEmail);
        user.removeFavoriteOffer(offer);
        userRepository.save(user);
    }

    public void removeOfferFromFavorites(Offer offer) {
        userRepository.findAll().forEach(user -> {
            user.removeFavoriteOffer(offer);
            userRepository.save(user);
        });
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User createUser(RegisterDTO registerDTO) {
        String encryptedPassword = passwordEncoder.encode(registerDTO.password());
        User user = new User();
        user.setEmail(registerDTO.email());
        user.setPassword(encryptedPassword);
        user.setPhoneNumber(registerDTO.phoneNumber());
        user.setName(registerDTO.name());

        Role role = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role newRole = new Role("ROLE_USER");
            return roleRepository.save(newRole);
        });
        user.addRole(role);

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

    private User getUserByEmail(String userEmail) throws UserWithEmailNotFoundException {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserWithEmailNotFoundException(userEmail));
    }

    private User getUserById(long id) throws UserWithIdNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserWithIdNotFoundException(id));
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UserWithEmailNotFoundException(username));
    }
}
