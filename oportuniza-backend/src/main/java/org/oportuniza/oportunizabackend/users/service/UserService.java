package org.oportuniza.oportunizabackend.users.service;

import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
    }

    public User updateUser(Long userId, User updatedUser) {
        User user = getUserById(userId); // change this
        user.setName(updatedUser.getName());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setResumeUrl(updatedUser.getResumeUrl());
        return userRepository.save(user);
    }

    public List<User> getFavoriteUsers(Long userId) {
        User user = getUserById(userId);
        return user.getFavoriteUsers();
    }

}
