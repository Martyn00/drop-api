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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        fileUploader.uploadFile(getInputStream(file), contentFileModel.getPath());
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

    public Resource download(File fileToDownload) {
        try {
            Path file = Paths.get(fileToDownload.getPath());
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }


    private InputStream getInputStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (IOException e) {
            throw new FolderException("Could not upload file");
        }
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


    private FileTypeModel getFileType(String fileTypeName) {
        FileTypeModel fileTypeModel = fileTypeService.getAllFileTypes().stream().
                filter(fileType -> fileType.getTypeName()
                        .equals(fileTypeName)).findFirst().get();
        return fileTypeModel;
    }
}
