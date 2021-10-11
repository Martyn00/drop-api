package com.util;

import com.exception.ServiceException;
import com.persistence.model.ContentFileModel;
import com.persistence.model.RootFolderModel;
import com.service.ContentFileService;
import com.service.RootFolderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FilePathChanger {
    private final ContentFileService contentFileService;
    private final RootFolderService rootFolderService;
    private final FileUtil fileUtil;


    public void updateFilesOnMove(ContentFileModel fileModel, String destinationUuid, String destinationPath) {
        deleteModelFromParents(fileModel);
        fileModel.setPath(destinationPath);
        connectFileModelToParentOnMove(fileModel, destinationUuid);
    }

    private void connectFileModelToParentOnMove(ContentFileModel newFileModel, String destinationUuid) {
        try {
            var parentFolder = contentFileService.getFileByUuid(destinationUuid);
            parentFolder.getSubFiles().add(newFileModel);
            newFileModel.setParentFolder(parentFolder);
            newFileModel.setRootFolder(parentFolder.getRootFolder());
            updateChildData(newFileModel, parentFolder.getRootFolder());
            contentFileService.save(parentFolder);
        } catch (ServiceException ex) {
            var rootFolder = rootFolderService.getRootFolderByUuid(destinationUuid);
            newFileModel.setRootFolder(rootFolder);
            rootFolder.getFiles().add(newFileModel);
            updateChildData(newFileModel, rootFolder);
            rootFolderService.saveRootFolder(rootFolder);
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
//        create new File with same things, create childs again add it to the destination folder (root or content)
        connectFileModelToParentOnCopy(fileModel, destinationUuid);
    }

    private void connectFileModelToParentOnCopy(ContentFileModel fileModel, String destinationUuid) {
        try {
            var parentFolder = contentFileService.getFileByUuid(destinationUuid);
            var newContentFile = copyAllFiles(fileModel, parentFolder.getRootFolder(), parentFolder);
            parentFolder.getSubFiles().add(newContentFile);
            contentFileService.save(parentFolder);
        } catch (ServiceException ex) {
            var rootFolder = rootFolderService.getRootFolderByUuid(destinationUuid);
            var newContentFile = copyAllFiles(fileModel, rootFolder, null);
            rootFolder.getFiles().add(newContentFile);
        }
    }

    private ContentFileModel copyAllFiles(ContentFileModel fileModel, RootFolderModel rootFolder, ContentFileModel parentFolder) {
        var newContentFile = fileUtil.setBasicData(fileModel.getFileName(), fileModel.getSize());
        newContentFile.setParentFolder(parentFolder);
        newContentFile.setRootFolder(rootFolder);
        newContentFile.setFileCreator(fileModel.getFileCreator());
        if (parentFolder == null) {
            newContentFile.setPath(rootFolder.getPath() + "/" + fileModel.getFileName());
        } else {
            newContentFile.setPath(parentFolder.getPath() + "/" + fileModel.getFileName());
        }
        newContentFile.setFileTypeModel(fileModel.getFileTypeModel());
        List<ContentFileModel> subFiles = fileModel.getSubFiles().stream().map(subFile -> copyAllFiles(subFile, rootFolder, fileModel)).collect(Collectors.toList());
        newContentFile.setSubFiles(subFiles);
        return newContentFile;
    }

}
