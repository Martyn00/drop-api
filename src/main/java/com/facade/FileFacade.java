package com.facade;

import com.controller.WebSocketController;
import com.exception.ServiceException;
import com.foldermanipulation.FileService;
import com.persistence.model.ContentFileModel;
import com.service.ContentFileService;
import com.service.RootFolderService;
import com.util.FilePathChanger;
import com.util.FileUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Component
@AllArgsConstructor
public class FileFacade {
    private final FileService fileService;

    private final ContentFileService contentFileService;
    private final RootFolderService rootFolderService;

    private final FileUtil fileUtil;

    private final FilePathChanger filePathChanger;

    private final WebSocketController webSocketController;

    public void uploadFile(MultipartFile file, String fileName, String parentUuid) {
        ContentFileModel contentFileModel = fileUtil.setBasicData(fileName, file.getSize(), file.getContentType());
        updateParents(contentFileModel, parentUuid);
        fileService.uploadFile(file, contentFileModel.getPath());
    }


    private void updateParents(ContentFileModel contentFileModel, String parentUuid) {
        try {
            var parentFolder = contentFileService.getFileByUuid(parentUuid);
            fileUtil.checkUniqueName(parentFolder, contentFileModel.getFileName());
            parentFolder.getSubFiles().add(contentFileModel);
            contentFileModel.setParentFolder(parentFolder);
            contentFileModel.setRootFolder(parentFolder.getRootFolder());
            contentFileModel.setPath(parentFolder.getPath() + "/" + contentFileModel.getFileName());
            contentFileModel.setFileCreator(parentFolder.getFileCreator());
            contentFileService.save(contentFileModel);
        } catch (ServiceException ex) {
            var rootFolder = rootFolderService.getRootFolderByUuid(parentUuid);
            fileUtil.checkUniqueName(rootFolder, contentFileModel.getFileName());
            rootFolder.getFiles().add(contentFileModel);
            contentFileModel.setRootFolder(rootFolder);
            contentFileModel.setPath(rootFolder.getPath() + "/" + contentFileModel.getFileName());
            contentFileModel.setFileCreator(rootFolder.getFolderCreator());
            contentFileService.save(contentFileModel);
        }
        webSocketController.notifySubscribersToFileChanges("", parentUuid);
    }

    public File getFile(String uuid) {
        ContentFileModel fileToDownload = contentFileService.getFileByUuid(uuid);
        return new File("../server/" + fileToDownload.getPath());
    }


    public void moveFile(String uuid, String destinationUuid, Boolean copy) {
        ContentFileModel fileModel = contentFileService.getFileByUuid(uuid);
        String newPath = getParentPath(destinationUuid) + "/" + fileModel.getFileName();
        if (copy.equals(false)) {
            fileService.moveFile("../server" + fileModel.getPath(), "../server" + newPath);
            filePathChanger.updateFilesOnMove(fileModel, destinationUuid, newPath);
        } else {
            fileService.copyFile("../server" + fileModel.getPath(), "../server" + newPath);
            filePathChanger.updateFilesOnCopy(fileModel, destinationUuid);
        }
        notifySubscribers(fileModel);
        webSocketController.notifySubscribersToFileChanges("", destinationUuid);
    }


    String getParentPath(String parentUuid) {
        try {
            return rootFolderService.getRootFolderByUuid(parentUuid).getPath();
        } catch (ServiceException ex) {
            return contentFileService.getFileByUuid(parentUuid).getPath();
        }
    }

    private void notifySubscribers(ContentFileModel contentFileModel) {
        if (contentFileModel.getParentFolder() != null) {
            webSocketController.notifySubscribersToFileChanges("", contentFileModel.getParentFolder().getUuid());
        } else {
            webSocketController.notifySubscribersToFileChanges("", contentFileModel.getRootFolder().getUuid());
        }
    }
}

