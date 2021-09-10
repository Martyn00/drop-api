package com.facade;

import com.exception.FolderException;
import com.exception.ServiceException;
import com.foldermanipulation.FileUploader;
import com.persistence.model.ContentFileModel;
import com.persistence.model.FileTypeModel;
import com.persistence.model.RootFolderModel;
import com.service.ContentFileService;
import com.service.FileTypeService;
import com.service.RootFolderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
@AllArgsConstructor
public class FileFacade {
    private final FileUploader fileUploader;
    private final ContentFileService contentFileService;
    private final RootFolderService rootFolderService;
    private final FileTypeService fileTypeService;

    public void uploadFile(InputStream inputStream, String fileName, String parentUuid) {
        ContentFileModel contentFileModel = new ContentFileModel();
        int size = 0;
        try {
            RootFolderModel parentDirectory = rootFolderService.getRootFolderByUuid(parentUuid);
            checkUniqueName(parentDirectory, fileName);
            size = fileUploader.uploadFile(inputStream, parentDirectory.getPath() + "/" + fileName);
            contentFileModel.setFileCreator(parentDirectory.getFolderCreator());
            contentFileModel.setPath(parentDirectory.getPath() + "/" + fileName);
            contentFileModel.setRootFolder(parentDirectory);
        } catch (ServiceException ex) {
            ContentFileModel parentDirectory = contentFileService.findContentFileModelByUuid(parentUuid);
            checkUniqueName(parentDirectory, fileName);
            size = fileUploader.uploadFile(inputStream, parentDirectory.getPath() + "/" + fileName);
            contentFileModel.setRootFolder(parentDirectory.getRootFolder());
            contentFileModel.setParentFolder(parentDirectory);
            contentFileModel.setFileCreator(parentDirectory.getFileCreator());
            contentFileModel.setPath(parentDirectory.getPath() + "/" + fileName);
        }
        contentFileModel.setUuid(UUID.randomUUID().toString());
        contentFileModel.setAddedDate(ZonedDateTime.now());
        contentFileModel.setLastModifiedDate(ZonedDateTime.now());
        contentFileModel.setSize((double) size);
        contentFileModel.setFileName(fileName);
        contentFileModel.setFileTypeModel(getFileType("text"));
        contentFileService.save(contentFileModel);
        updateParents(contentFileModel);
    }

    private void checkUniqueName(ContentFileModel parentDirectory, String fileName) {
        long count = parentDirectory.getSubFiles().stream().filter(file -> file.getFileName().equals(fileName)).count();
        if (count != 0) {
            throw new FolderException("File already exists in that folder");
        }
    }

    private void checkUniqueName(RootFolderModel parentDirectory, String fileName) {
        long count = parentDirectory.getFiles().stream().filter(file -> file.getFileName().equals(fileName)).count();
        if (count != 0) {
            throw new FolderException("File already exists in that folder");
        }
    }

    private void updateParents(ContentFileModel contentFileModel) {
        if (contentFileModel.getParentFolder() != null) {
            ContentFileModel conteFileParent = contentFileModel.getParentFolder();
            conteFileParent.getSubFiles().add(contentFileModel);
            contentFileService.save(conteFileParent);
        }
        RootFolderModel rootFolderModel = contentFileModel.getRootFolder();
        rootFolderModel.getFiles().add(contentFileModel);
        rootFolderService.saveRootFolder(rootFolderModel);
    }


    private FileTypeModel getFileType(String fileTypeName) {
        FileTypeModel fileTypeModel = fileTypeService.getAllFileTypes().stream().
                filter(fileType -> fileType.getTypeName()
                        .equals(fileTypeName)).findFirst().get();
//        fileTypeService.save(fileTypeModel);
        return fileTypeModel;
    }
}
