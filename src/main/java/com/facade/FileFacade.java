package com.facade;

import com.exception.FolderException;
import com.exception.ServiceException;
import com.foldermanipulation.FileService;
import com.persistence.model.ContentFileModel;
import com.persistence.model.FileTypeModel;
import com.persistence.model.RootFolderModel;
import com.service.ContentFileService;
import com.service.FileTypeService;
import com.service.RootFolderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
@AllArgsConstructor
public class FileFacade {
    private final FileService fileUploader;
    private final ContentFileService contentFileService;
    private final RootFolderService rootFolderService;
    private final FileTypeService fileTypeService;

    public void uploadFile(MultipartFile file, String fileName, String parentUuid) {
        ContentFileModel contentFileModel = setBasicData(fileName, file.getSize());
        updateParents(contentFileModel, parentUuid);
        fileUploader.uploadFile(file, contentFileModel.getPath());
    }

    private ContentFileModel setBasicData(String fileName, double size) {
        ContentFileModel contentFileModel = new ContentFileModel();
        contentFileModel.setUuid(UUID.randomUUID().toString());
        contentFileModel.setAddedDate(ZonedDateTime.now());
        contentFileModel.setLastModifiedDate(ZonedDateTime.now());
        contentFileModel.setSize(size);
        contentFileModel.setFileName(fileName);
        contentFileModel.setFileTypeModel(getFileType("text"));
        return contentFileModel;
    }

    private void updateParents(ContentFileModel contentFileModel, String parentUuid) {
        try {
            var parentFolder = contentFileService.getFileByUuid(parentUuid);
            checkUniqueName(parentFolder, contentFileModel.getFileName());
            parentFolder.getSubFiles().add(contentFileModel);
            contentFileModel.setParentFolder(parentFolder);
            contentFileModel.setRootFolder(parentFolder.getRootFolder());
            contentFileModel.setPath(parentFolder.getPath() + "/" + contentFileModel.getFileName());
            contentFileModel.setFileCreator(parentFolder.getFileCreator());
            contentFileService.save(parentFolder);
        } catch (ServiceException ex) {
            var rootFolder = rootFolderService.getRootFolderByUuid(parentUuid);
            checkUniqueName(rootFolder, contentFileModel.getFileName());
            rootFolder.getFiles().add(contentFileModel);
            contentFileModel.setRootFolder(rootFolder);
            contentFileModel.setPath(rootFolder.getPath() + "/" + contentFileModel.getFileName());
            contentFileModel.setFileCreator(rootFolder.getFolderCreator());
            rootFolderService.saveRootFolder(rootFolder);
        }

    }

    public File getFile(String uuid) {
        ContentFileModel fileToDownload = contentFileService.getFileByUuid(uuid);
        return new File("../server/" + fileToDownload.getPath());
    }

    public void checkUniqueName(ContentFileModel parentDirectory, String fileName) {
        long count = parentDirectory.getSubFiles().stream().filter(file -> file.getFileName().equals(fileName)).count();
        if (count != 0) {
            throw new FolderException("File already exists in that folder");
        }
    }

    public void checkUniqueName(RootFolderModel parentDirectory, String fileName) {
        long count = parentDirectory.getFiles().stream().filter(file -> file.getFileName().equals(fileName)).count();
        if (count != 0) {
            throw new FolderException("File already exists in that folder");
        }
    }


    private FileTypeModel getFileType(String fileTypeName) {
        FileTypeModel fileTypeModel = fileTypeService.getAllFileTypes().stream().
                filter(fileType -> fileType.getTypeName()
                        .equals(fileTypeName)).findFirst().get();
        return fileTypeModel;
    }
}
