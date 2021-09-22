package com.facade;

import com.controller.dto.AuthenticationDto;
import com.controller.dto.DisplayUserDto;
import com.controller.dto.LoggedResponseDto;
import com.controller.dto.UserDto;
import com.exception.InvalidCredentialsException;
import com.exception.UserNotFoundException;
import com.persistence.model.RootFolderModel;
import com.persistence.model.UserModel;
import com.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class UserFacade {
    private static final String INVALID_USER_OR_PASSWORD = "Invalid username or password";

    private final UserService userService;
    private final RootFolderFacade rootFolderFacade;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder encoder;

    public DisplayUserDto createUser(UserDto userDto) {
        UserModel userModel = modelMapper.map(userDto, UserModel.class);
        userModel.setPassword(encoder.encode(userDto.getPassword()));
        DisplayUserDto displayUserDto = modelMapper.map(userService.saveUser(userModel), DisplayUserDto.class);
        List<RootFolderModel> rootFolders = rootFolderFacade.createRootFoldersForUser(userModel);
        displayUserDto.setPrivateUuid(getPrivateFolderUuid(rootFolders));
        return displayUserDto;
    }

    private String getPrivateFolderUuid(List<RootFolderModel> rootFolders) {
        return rootFolders
                .stream()
                .filter(rootFolder -> rootFolder.getShared() == false)
                .findFirst().get().getUuid();
    }

    public LoggedResponseDto createLoggedResponse(String token, AuthenticationDto authenticationDto) {
        UserModel userModel = userService.findUserByUsername(authenticationDto.getUsername());
        DisplayUserDto displayUserDto = modelMapper.map(userModel, DisplayUserDto.class);
        displayUserDto.setPrivateUuid(getPrivateFolderUuid(rootFolderFacade.getRootFolderByUser(userModel)));
        return new LoggedResponseDto(token, displayUserDto);
    }

    public void checkCredentials(AuthenticationDto authenticationDto) {
        try {
            UserModel userModel = userService.findUserByUsername(authenticationDto.getUsername());
            if (!userService.isPasswordValid(authenticationDto.getPassword(), userModel.getPassword())) {
                throw new InvalidCredentialsException(INVALID_USER_OR_PASSWORD);
            }
        } catch(UserNotFoundException exception){
            throw new InvalidCredentialsException(INVALID_USER_OR_PASSWORD);
        }
    }

    public Boolean checkUserExistsByEmail(String email) {
        return userService.checkUserExistsByEmail(email);
    }

    public Boolean checkUserExistsByUsername(String username) {
        return userService.checkUserExistsByUsername(username);
    }
}
