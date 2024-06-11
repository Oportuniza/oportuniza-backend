package org.oportuniza.oportunizabackend.users.service;

import org.oportuniza.oportunizabackend.applications.model.Application;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.offers.dto.OfferDTO;
import org.oportuniza.oportunizabackend.offers.model.Offer;
import org.oportuniza.oportunizabackend.offers.repository.JobRepository;
import org.oportuniza.oportunizabackend.offers.service.OfferService;
import org.oportuniza.oportunizabackend.users.dto.UpdateUserDTO;
import org.oportuniza.oportunizabackend.users.dto.UserDTO;
import org.oportuniza.oportunizabackend.users.exceptions.*;
import org.oportuniza.oportunizabackend.users.model.Role;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.repository.FavoriteOffersRepository;
import org.oportuniza.oportunizabackend.users.repository.RoleRepository;
import org.oportuniza.oportunizabackend.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FavoriteOffersRepository favoriteOffersRepository;

    public UserService(final UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JobRepository jobRepository, FavoriteOffersRepository favoriteOffersRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.favoriteOffersRepository = favoriteOffersRepository;
    }

    public UserDTO getUser(long userId) throws UserNotFoundException {
        User user = getUserById(userId);
        return convertToUserDTO(user);
    }

    public UserDTO updateUser(long userId, UpdateUserDTO updatedUser)
            throws UserNotFoundException, OldPasswordNotProvided, NewPasswordNotProvided{
        User user = getUserById(userId);

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
        } else if (newPassword != null) {
            throw new OldPasswordNotProvided();
        }
    }

    public Page<UserDTO> getFavoriteUsers(long userId, int page, int size) throws UserNotFoundException {
        return userRepository.findFavoriteUsersByUserId(userId, PageRequest.of(page, size)).map(this::convertToUserDTO);
    }

    public Page<OfferDTO> getFavoriteOffers(long userId, int page, int size) throws UserNotFoundException {
        return favoriteOffersRepository.findFavoriteOffersByUserId(userId, PageRequest.of(page, size)).map(OfferService::convertToDTO);
    }

    public void addFavoriteUser(long userId, long id) throws UserNotFoundException {
        User user = getUserById(userId);
        User favoriteUser = getUserById(id);
        user.addFavoriteUser(favoriteUser);
        userRepository.save(user);
    }

    public void removeFavoriteUser(long userId, long id) throws UserNotFoundException {
        User user = getUserById(userId);
        User favoriteUser = getUserById(id);
        user.removeFavoriteUser(favoriteUser);
        userRepository.save(user);
    }

    public void addOffer(long id, Offer offer) throws UserNotFoundException {
        User user = getUserById(id);
        user.addOffer(offer);
        userRepository.save(user);
    }

    public void removeOffer(Offer offer) {
            var user = offer.getUser();
            user.removeOffer(offer);
            userRepository.save(user);
    }

    public void addFavoriteOffer(long userId, Offer offer) throws UserNotFoundException {
        User user = getUserById(userId);
        user.addFavoriteOffer(offer);
        userRepository.save(user);
    }

    public void removeFavoriteOffer(long userId, Offer offer) throws UserNotFoundException {
        User user = getUserById(userId);
        user.removeFavoriteOffer(offer);
        userRepository.save(user);
    }

    public void removeOfferFromFavorites(Offer offer) {
        userRepository.findAll().forEach(user -> {
            user.removeFavoriteOffer(offer);
            userRepository.save(user);
        });
    }

    public void addApplication(User user, Application application) {
        user.addApplication(application);
        userRepository.save(user);
    }

    public void removeApplication(Application application) {
        var user = application.getUser();
        user.removeApplication(application);
        userRepository.save(user);
    }

    public void updateUserRating(long userId, double averageRating) throws UserNotFoundException {
        User user = getUserById(userId);
        user.setAverageRating(averageRating);
        user.incrementReviewCount();
        userRepository.save(user);
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
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getDistrict(),
                user.getCounty(),
                user.getResumeUrl(),
                user.getAverageRating(),
                user.getReviewCount());
    }

    public User getUserById(long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User loadUserByUsername(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
