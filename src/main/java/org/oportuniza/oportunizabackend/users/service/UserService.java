package org.oportuniza.oportunizabackend.users.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.oportuniza.oportunizabackend.applications.model.Application;
import org.oportuniza.oportunizabackend.authentication.dto.RegisterDTO;
import org.oportuniza.oportunizabackend.offers.dto.OfferDTO;
import org.oportuniza.oportunizabackend.offers.model.Offer;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.Date;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FavoriteOffersRepository favoriteOffersRepository;
    private final GoogleCloudStorageService googleCloudStorageService;
    private final GoogleIdTokenVerifier verifier;

    public UserService(final UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, FavoriteOffersRepository favoriteOffersRepository, GoogleCloudStorageService googleCloudStorageService, GoogleIdTokenVerifier verifier) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.favoriteOffersRepository = favoriteOffersRepository;
        this.googleCloudStorageService = googleCloudStorageService;
        this.verifier = verifier;
    }

    private UserDTO verifyIDToken(String idToken) {
        try {
            GoogleIdToken idTokenObj = verifier.verify(idToken);
            if (idTokenObj == null) {
                return null;
            }
            GoogleIdToken.Payload payload = idTokenObj.getPayload();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            String email = payload.getEmail();
            String pictureUrl = (String) payload.get("picture");

            // todo :
            // return new UserDTO(firstName, lastName, email, pictureUrl);
        } catch (GeneralSecurityException | IOException e) {
            return null;
        }
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public UserDTO getUser(long userId) throws UserNotFoundException {
        User user = getUserById(userId);
        return convertToDTO(user);
    }

    public UserDTO updateUser(long userId, UpdateUserDTO updatedUser, MultipartFile profileImage, MultipartFile resumeFile)
            throws UserNotFoundException, OldPasswordNotProvided, NewPasswordNotProvided, PasswordMismatchException, IOException, URISyntaxException {
        User user = getUserById(userId);

        if (updatedUser.name() != null) {
            user.setName(updatedUser.name());
        }
        if (updatedUser.phoneNumber() != null) {
            user.setPhoneNumber(updatedUser.phoneNumber());
        }
        if (updatedUser.district() != null) {
            user.setDistrict(updatedUser.district());
        }
        if (updatedUser.county() != null) {
            user.setCounty(updatedUser.county());
        }
        if (profileImage != null && !profileImage.isEmpty()) {
            if (user.getPictureUrl() != null && user.getPictureName() != null && !user.getPictureName().isEmpty()) {
                googleCloudStorageService.deleteFile(user.getPictureName());
            }
            var pictureUrl = googleCloudStorageService.uploadFile(profileImage);
            user.setPictureUrl(pictureUrl.getValue1());
            user.setPictureName(pictureUrl.getValue0());
        }
        if (resumeFile != null && !resumeFile.isEmpty()) {
            if (user.getResumeUrl() != null && user.getResumeName() != null && !user.getResumeName().isEmpty()) {
                googleCloudStorageService.deleteFile(user.getResumeName());
            }
            var resumeUrl = googleCloudStorageService.uploadFile(resumeFile);
            user.setResumeUrl(resumeUrl.getValue1());
            user.setResumeName(resumeUrl.getValue0());
            user.setResumeFileName(updatedUser.resumeFileName());
        }

        updatePasswordIfProvided(user, updatedUser);
        userRepository.save(user);

        return convertToDTO(user);
    }

    private void updatePasswordIfProvided(User user, UpdateUserDTO updatedUser)
            throws NewPasswordNotProvided, OldPasswordNotProvided, PasswordMismatchException {
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

    public Page<UserDTO> getFavoriteUsers(long userId, int page, int size) {
        return userRepository.findFavoriteUsersByUserId(userId, PageRequest.of(page, size)).map(this::convertToDTO);
    }

    public Page<OfferDTO> getFavoriteOffers(long userId, int page, int size) {
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

    public void removePicture(long userId){
        User user = getUserById(userId);
        googleCloudStorageService.deleteFile(user.getPictureName());
        user.setPictureUrl(null);
        user.setPictureName(null);
        userRepository.save(user);
    }

    public void removeResume(long userId){
        User user = getUserById(userId);
        googleCloudStorageService.deleteFile(user.getResumeName());
        user.setResumeUrl(null);
        user.setResumeName(null);
        userRepository.save(user);
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
        user.setDistrict(registerDTO.district());
        user.setCounty(registerDTO.county());
        user.setAuthProvider("local");
        user.setLastActivityAt(new Date());

        Role role = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role newRole = new Role("ROLE_USER");
            return roleRepository.save(newRole);
        });
        user.addRole(role);

        return userRepository.save(user);
    }

    public UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getDistrict(),
                user.getCounty(),
                user.getResumeUrl(),
                user.getResumeName(),
                user.getResumeFileName(),
                user.getPictureUrl(),
                user.getPictureName(),
                user.getAverageRating(),
                user.getReviewCount(),
                user.getLastActivityAt(),
                user.getCreatedAt());
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
