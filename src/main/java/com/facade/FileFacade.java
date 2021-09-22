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
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class FileFacade {
    private final FileService fileUploader;
    private final ContentFileService contentFileService;
    private final RootFolderService rootFolderService;
    private final FileTypeService fileTypeService;

    public void uploadFile(MultipartFile file, String fileName, String parentUuid) {
        ContentFileModel contentFileModel = new ContentFileModel();
        InputStream inputStream = getInputStream(file);
        int size = 0;
        try {
            RootFolderModel parentDirectory = rootFolderService.getRootFolderByUuid(parentUuid);
            checkUniqueName(parentDirectory, fileName);
//            size = fileUploader.uploadFile(inputStream, parentDirectory.getPath() + "/" + fileName);
            contentFileModel.setFileCreator(parentDirectory.getFolderCreator());
            contentFileModel.setPath(parentDirectory.getPath() + "/" + fileName);
            contentFileModel.setRootFolder(parentDirectory);
        } catch (ServiceException ex) {
            ContentFileModel parentDirectory = contentFileService.findContentFileModelByUuid(parentUuid);
            checkUniqueName(parentDirectory, fileName);
//            size = fileUploader.uploadFile(inputStream, parentDirectory.getPath() + "/" + fileName);
            contentFileModel.setRootFolder(parentDirectory.getRootFolder());
            contentFileModel.setParentFolder(parentDirectory);
            contentFileModel.setFileCreator(parentDirectory.getFileCreator());
            contentFileModel.setPath(parentDirectory.getPath() + "/" + fileName);
        }
        updateParents(contentFileModel, fileName, parentUuid);
    }

    private ContentFileModel setData(String fileName, ContentFileModel contentFileModel, double size) {
        contentFileModel.setUuid(UUID.randomUUID().toString());
        contentFileModel.setAddedDate(ZonedDateTime.now());
        contentFileModel.setLastModifiedDate(ZonedDateTime.now());
        contentFileModel.setSize(size);
        contentFileModel.setFileName(fileName);
        contentFileModel.setFileTypeModel(getFileType("text"));
        return contentFileService.save(contentFileModel);
    }

    private void updateParents(ContentFileModel contentFileModel, String filename, String parentUuid) {
        contentFileModel = setData(filename, contentFileModel, 0);
        if (contentFileModel.getParentFolder() != null) {
            ContentFileModel conteFileParent = contentFileService.getFileByUuid(contentFileModel.getParentFolder().getUuid());
            conteFileParent.getSubFiles().add(contentFileModel);
            contentFileService.save(conteFileParent);
        } else {
            RootFolderModel rootFolderModel = rootFolderService.getRootFolderByUuid(contentFileModel.getRootFolder().getUuid());
            List<ContentFileModel> contentFileModelList = rootFolderModel.getFiles();
            System.out.println(contentFileModelList);
            contentFileModelList.add(contentFileModel);
            System.out.println(contentFileModelList);
            rootFolderModel.setFiles(contentFileModelList);
            rootFolderService.saveRootFolder(rootFolderModel);
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
