package com.service;

import com.exception.UserNotFoundException;
import com.persistence.model.UserModel;
import com.persistence.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;

@Component
@AllArgsConstructor
@Getter
@Setter
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserModel saveUser(UserModel userModel) {
        userModel.setUuid(UUID.randomUUID().toString());
        return userRepository.save(userModel);
    }

    public UserModel findUserByUsername(String username) {
        return userRepository.findUserModelByUsername(username).orElseThrow(() -> {
            throw new UserNotFoundException(MessageFormat.format("User {0} not found!", username));
        });
    }

    public boolean isPasswordValid(String rawPassword, String encryptedPassword) {
        return encoder.matches(rawPassword, encryptedPassword);
    }
}
