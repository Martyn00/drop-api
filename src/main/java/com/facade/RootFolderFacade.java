package com.facade;

import com.controller.dto.AddedUserDto;
import com.exception.AuthorizationException;
import com.exception.FacadeException;
import com.foldermanipulation.RootFolderCreator;
import com.persistence.model.RootFolderModel;
import com.persistence.model.UserModel;
import com.service.RootFolderService;
import com.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RootFolderFacade {

    private static final String PRIVATE = "My drive";
    private static final String PATH_SEPARATOR = "/";
    private static final String SHARED = "shared";
    private final RootFolderCreator rootFolderCreator;
    private final AuthenticationFacade authenticationFacade;
    private final RootFolderService rootFolderService;
    private final UserService userService;

    public RootFolderModel createPrivateRootFolderForUser(UserModel userModel) {
        RootFolderModel privateRootFolder = createPrivateFolder(userModel);
//        creates physically the root folders
        rootFolderCreator.createBasicUserFolders(userModel.getUsername());
        return privateRootFolder;
    }

    private RootFolderModel createPrivateFolder(UserModel userModel) {
        return createFolderModel(userModel, PRIVATE, Boolean.FALSE, PATH_SEPARATOR + userModel.getUsername() + PATH_SEPARATOR + PRIVATE);
    }

    private RootFolderModel createFolderModel(UserModel userModel, String folderName, Boolean isShared, String path) {
        RootFolderModel rootFolderModel = new RootFolderModel();
        rootFolderModel.setFolderCreator(userModel);
        rootFolderModel.setUuid(UUID.randomUUID().toString());
        rootFolderModel.setFileName(folderName);
        rootFolderModel.setShared(isShared);
        rootFolderModel.setPath(path);
        rootFolderModel.setFiles(Collections.emptyList());
        rootFolderModel.setAllowedUsers(new ArrayList<>());
        rootFolderModel.getAllowedUsers().add(userModel);
        return rootFolderModel;
    }

    public RootFolderModel createSharedFolder(String folderName) {
        UserModel userModel = authenticationFacade.getUserFromSecurityContext();
        rootFolderCreator.createRootFolder(PATH_SEPARATOR + userModel.getUsername() + PATH_SEPARATOR + folderName);
        RootFolderModel rootFolderModel = createFolderModel(userModel, folderName, true, PATH_SEPARATOR + userModel.getUsername() + PATH_SEPARATOR + folderName);
        userModel.getAccessibleRootFolders().add(rootFolderModel);
        return rootFolderService.saveRootFolder(rootFolderModel);
    }

    public void addUsersToSharedFolder(String folderUuid, List<AddedUserDto> addedUserDtos) {
        List<UserModel> users = addedUserDtos
                .stream().map(addedUserDto -> userService.getUserByUuid(addedUserDto.getUuid()))
                .collect(Collectors.toList());
        RootFolderModel rootFolderModel = rootFolderService.getRootFolderByUuid(folderUuid);
        users.forEach(user -> user.getAccessibleRootFolders().add(rootFolderModel));
        validateAddition(rootFolderModel, users);
        rootFolderModel.getAllowedUsers().addAll(users);
        rootFolderService.saveRootFolder(rootFolderModel);
    }

    private void validateAddition(RootFolderModel rootFolder, List<UserModel> users) {
        if (rootFolder.getShared().equals(false)) {
            throw new FacadeException("Folder is private");
        }
        if (users.stream().anyMatch(user -> rootFolder.getAllowedUsers().contains(user))) {
            throw new FacadeException("An user is already added in the folder access table");
        }
        if (!rootFolder.getFolderCreator().getUuid().equals(authenticationFacade.getUserFromSecurityContext().getUuid())) {
            throw new AuthorizationException("User does not have authorization for adding other users");
        }
    }

    private RootFolderModel createSharedFolder(UserModel userModel) {
        return createFolderModel(userModel, SHARED, Boolean.TRUE, PATH_SEPARATOR + userModel.getUsername() + PATH_SEPARATOR + SHARED);
    }

}
