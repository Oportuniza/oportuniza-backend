package org.oportuniza.oportunizabackend.users.service;

import org.oportuniza.oportunizabackend.users.exceptions.UserWithEmailNotFoundException;
import org.oportuniza.oportunizabackend.users.model.User;
import org.oportuniza.oportunizabackend.users.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DetailsUserService implements UserDetailsService {
    private final UserRepository userRepository;

    public DetailsUserService(final UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserWithEmailNotFoundException(email));
    }


}
