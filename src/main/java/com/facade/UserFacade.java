package com.facade;

import com.controller.dto.UserDto;
import com.persistence.model.UserModel;
import com.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final RootFolderFacade rootFolderFacade;
    private final ModelMapper modelMapper;

    public UserDto createUser(UserDto userDto) {
        UserModel userModel = modelMapper.map(userDto, UserModel.class);
        UserDto obtainedUserDto = modelMapper.map(userService.saveUser(userModel), UserDto.class);
        rootFolderFacade.createRootFoldersForUser(userModel);
        return obtainedUserDto;
    }

}
