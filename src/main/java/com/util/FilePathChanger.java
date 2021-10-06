package com.util;

import com.exception.ServiceException;
import com.persistence.model.ContentFileModel;
import com.persistence.model.RootFolderModel;
import com.service.ContentFileService;
import com.service.RootFolderService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class FilePathChanger {
    private final ContentFileService contentFileService;
    private final RootFolderService rootFolderService;
    private final ModelMapper modelMapper;

    public void updateFilesOnMove(ContentFileModel fileModel, String destinationUuid, String destinationPath) {
        deleteModelFromParents(fileModel);
        fileModel.setPath(destinationPath);
        connectFileModelToParent(fileModel, destinationUuid);
    }

    private void connectFileModelToParent(ContentFileModel newFileModel, String destinationUuid) {
        try {
            var rootFolder = rootFolderService.getRootFolderByUuid(destinationUuid);
            newFileModel.setRootFolder(rootFolder);
            rootFolder.getFiles().add(newFileModel);
            updateChildData(newFileModel, rootFolder);
            rootFolderService.saveRootFolder(rootFolder);
        } catch (ServiceException ex) {
            var parentFolder = contentFileService.getFileByUuid(destinationUuid);
            parentFolder.getSubFiles().add(newFileModel);
            newFileModel.setParentFolder(parentFolder);
            newFileModel.setRootFolder(parentFolder.getRootFolder());
            updateChildData(newFileModel, parentFolder.getRootFolder());
            contentFileService.save(parentFolder);
        }
    }

    private void updateChildData(ContentFileModel contentFileModel, RootFolderModel rootFolder) {
        List<ContentFileModel> children = contentFileModel.getSubFiles();
        children.forEach(child -> child.setPath(contentFileModel.getPath() + "/" + child.getFileName()));
        children.forEach(child -> child.setRootFolder(rootFolder));
        children.forEach(child -> updateChildData(child, rootFolder));
        contentFileService.batchSave(children);
    }

    private void deleteModelFromParents(ContentFileModel fileModel) {
        if (fileModel.getParentFolder() != null) {
            var parentFolder = fileModel.getParentFolder();
            parentFolder.getSubFiles().remove(fileModel);
        } else {
            var rootFolder = fileModel.getRootFolder();
            rootFolder.getFiles().remove(fileModel);
        }
    }

    public void updateFilesOnCopy(ContentFileModel fileModel, String destinationUuid) {
    }
}
