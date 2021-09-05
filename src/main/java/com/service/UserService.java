package com.service;

import com.exception.ServiceException;
import com.persistence.model.UserModel;
import com.persistence.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserModel saveUser(UserModel userModel) {
        userModel.setUuid(UUID.randomUUID().toString());
        return userRepository.save(userModel);
    }

    public UserModel getUserByUuid(String uuid) {
        return userRepository.findUserModelByUuid(uuid).orElseThrow(() -> {
            throw new ServiceException("The user does not exist");
        });
    }
}
