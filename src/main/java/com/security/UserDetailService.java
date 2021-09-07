package com.security;

import com.persistence.model.UserModel;
import com.persistence.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

    private static final String USER_HAS_NOT_BEEN_FOUND_MESSAGE = "User has not been found";
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel userCredential = retrieveUserIfExists(userRepository.findUserModelByUsername(username));
        String credentialUsername = userCredential.getUsername();
        String password = userCredential.getPassword();
        return new User(credentialUsername, password, Collections.emptyList());
    }

    private UserModel retrieveUserIfExists(Optional<UserModel> userCredentialOptional) {
        if (userCredentialOptional.isEmpty()) {
            throw new UsernameNotFoundException(USER_HAS_NOT_BEEN_FOUND_MESSAGE);
        }
        return userCredentialOptional.get();
    }
}
