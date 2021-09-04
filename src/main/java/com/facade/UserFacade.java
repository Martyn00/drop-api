package com.facade;

import com.controller.dto.*;
import com.exception.InvalidCredentialsException;
import com.exception.UserNotFoundException;
import com.persistence.model.UserModel;
import com.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserFacade {
    private static final String INVALID_USER_OR_PASSWORD = "Invalid username or password";

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder encoder;

    public DisplayUserDto createUser(UserDto userDto) {
        UserModel userModel = modelMapper.map(userDto, UserModel.class);
        userModel.setPassword(encoder.encode(userDto.getPassword()));
        return modelMapper.map(userService.saveUser(userModel), DisplayUserDto.class);
    }

    public LoggedResponseDto createLoggedResponse(String token, AuthenticationDto authenticationDto){
        UserModel userModel = userService.findUserByUsername(authenticationDto.getUsername());
        LoggedUserDto loggedUserDto = modelMapper.map(userModel, LoggedUserDto.class);
        return new LoggedResponseDto(token, loggedUserDto);
    }

    public void checkCredentials(AuthenticationDto authenticationDto){
        try {
            UserModel userModel = userService.findUserByUsername(authenticationDto.getUsername());
            if (!userService.isPasswordValid(authenticationDto.getPassword(), userModel.getPassword())) {
                throw new InvalidCredentialsException(INVALID_USER_OR_PASSWORD);
            }
        } catch(UserNotFoundException exception){
            throw new InvalidCredentialsException(INVALID_USER_OR_PASSWORD);
        }
    }
}
