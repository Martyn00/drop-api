package com.service;

import com.persistence.model.UserModel;
import com.persistence.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
@Getter
@Setter
public class UserService {

    private final UserRepository userRepository;

    public UserModel saveUser(UserModel userModel){
        userModel.setUuid(UUID.randomUUID().toString());
        return userRepository.save(userModel);
    }
}
