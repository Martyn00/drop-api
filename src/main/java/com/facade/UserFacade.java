package com.facade;

import com.controller.dto.*;
import com.exception.InvalidCredentialsException;
import com.exception.UserNotFoundException;
import com.persistence.model.RootFolderModel;
import com.persistence.model.UserModel;
import com.service.RootFolderService;
import com.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserFacade {
    private static final String INVALID_USER_OR_PASSWORD = "Invalid username or password";

    private final UserService userService;
    private final RootFolderFacade rootFolderFacade;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder encoder;
    private final RootFolderService rootFolderService;

    public DisplayUserDto createUser(UserDto userDto) {
        UserModel userModel = modelMapper.map(userDto, UserModel.class);
        userModel.setPassword(encoder.encode(userDto.getPassword()));
        userModel.setAccessibleRootFolders(new ArrayList<>());
        userModel.getAccessibleRootFolders().add(rootFolderFacade.createPrivateRootFolderForUser(userModel));
        return modelMapper.map(userService.saveUser(userModel), DisplayUserDto.class);
    }

    public LoggedResponseDto createLoggedResponse(String token, AuthenticationDto authenticationDto) {
        UserModel userModel = userService.findUserByUsername(authenticationDto.getUsername());
        DisplayUserDto displayUserDto = modelMapper.map(userModel, DisplayUserDto.class);
        String rootFolderUuid = userModel.getAccessibleRootFolders()
                .stream()
                .filter(rootFolderModel -> !rootFolderModel.getShared())
                .findFirst().get().getUuid();
        displayUserDto.setPrivateUuid(rootFolderUuid);
        return new LoggedResponseDto(token, displayUserDto);
    }

    public void checkCredentials(AuthenticationDto authenticationDto) {
        try {
            UserModel userModel = userService.findUserByUsername(authenticationDto.getUsername());
            if (!userService.isPasswordValid(authenticationDto.getPassword(), userModel.getPassword())) {
                throw new InvalidCredentialsException(INVALID_USER_OR_PASSWORD);
            }
        } catch (UserNotFoundException exception) {
            throw new InvalidCredentialsException(INVALID_USER_OR_PASSWORD);
        }
    }

    public Boolean checkUserExistsByEmail(String email) {
        return userService.checkUserExistsByEmail(email);
    }

    public Boolean checkUserExistsByUsername(String username) {
        return userService.checkUserExistsByUsername(username);
    }

    List<RootFolderModel> getAllRootFoldersByUserUuid(String uuid) {
        return userService.getUserByUuid(uuid).getAccessibleRootFolders();
    }

    public List<PossibleUserDto> getUsersToBeAdded(String rootFolderUuid) {
        RootFolderModel rootFolderModel = rootFolderService.getRootFolderByUuid(rootFolderUuid);
        List<String> uuids = rootFolderModel.getAllowedUsers()
                .stream().map(UserModel::getUuid)
                .collect(Collectors.toList());

        return userService.getAllUsers()
                .stream()
                .filter(user -> !uuids.contains(user.getUuid()))
                .map(userModel -> modelMapper.map(userModel, PossibleUserDto.class))
                .collect(Collectors.toList());
    }

    public List<PossibleUserDto> getUsersAlreadyAdded(String rootFolderUuid) {
        RootFolderModel rootFolderModel = rootFolderService.getRootFolderByUuid(rootFolderUuid);
        List<String> uuids = rootFolderModel.getAllowedUsers()
                .stream().map(UserModel::getUuid)
                .collect(Collectors.toList());
        return userService.getAllUsers()
                .stream()
                .filter(user -> uuids.contains(user.getUuid()))
                .filter(user -> !user.getUsername().equals(getUsernameFromContext()))
                .map(userModel -> modelMapper.map(userModel, PossibleUserDto.class))
                .collect(Collectors.toList());
    }

    private String getUsernameFromContext() {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return details.getUsername();
    }
}
