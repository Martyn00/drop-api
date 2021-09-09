package com.facade;

import com.foldermanipulation.RootFolderCreator;
import com.persistence.model.RootFolderModel;
import com.persistence.model.UserModel;
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

    public List<RootFolderModel> createRootFoldersForUser(UserModel userModel) {
        List<RootFolderModel> rootFolderModels = new ArrayList<>();
        rootFolderModels.add(createPrivateFolder(userModel));
        rootFolderModels.add(createSharedFolder(userModel));
        rootFolderCreator.createRootFolders(userModel.getUsername());
        return rootFolderService.batchSaveRootFolders(rootFolderModels);
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
        rootFolderModel.setFiles(Collections.emptyList());
        return rootFolderModel;
    }
}
