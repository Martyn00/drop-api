package com.facade;

import com.foldermanipulation.RootFolderCreator;
import com.persistence.model.RootFolderAccessModel;
import com.persistence.model.RootFolderModel;
import com.persistence.model.UserModel;
import com.service.RootFolderAccessService;
import com.service.RootFolderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class RootFolderFacade {

    private static final String PRIVATE = "private";
    private static final String SHARED = "shared";
    private static final String PATH_SEPARATOR = "/";

    private RootFolderService rootFolderService;
    private RootFolderCreator rootFolderCreator;
    private final RootFolderAccessService rootFolderAccessService;

    public List<RootFolderModel> createRootFoldersForUser(UserModel userModel) {
        List<RootFolderModel> rootFolderModels = new ArrayList<>();
        RootFolderModel privateRootFolder = createPrivateFolder(userModel);
        RootFolderModel sharedRootFolder = createSharedFolder(userModel);
        RootFolderAccessModel privateRootFolderAccessModel = new RootFolderAccessModel();
        privateRootFolderAccessModel.setUsers(Collections.singletonList(userModel));
        privateRootFolderAccessModel.setRootFolders(Collections.singletonList(privateRootFolder));
        privateRootFolder.getRootFolderAccessModel().add(privateRootFolderAccessModel);
        rootFolderModels.add(privateRootFolder);
        rootFolderModels.add(sharedRootFolder);
        rootFolderCreator.createRootFolders(userModel.getUsername());
        return rootFolderService.batchSaveRootFolders(rootFolderModels);
    }

    public List<RootFolderModel> getRootFolderByUser(UserModel userModel) {
        return rootFolderService.getAllRootFoldersByUserUuid(userModel);
    }

    private RootFolderModel createSharedFolder(UserModel userModel) {
        return createFolderModel(userModel, SHARED, Boolean.TRUE, PATH_SEPARATOR + userModel.getUsername() + PATH_SEPARATOR + SHARED);
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
        rootFolderModel.setFiles(new ArrayList<>());
        rootFolderModel.setRootFolderAccessModel(new ArrayList<>());
        return rootFolderModel;
    }
}
