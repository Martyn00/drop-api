package com.facade;

import com.controller.dto.ContentFileDto;
import com.controller.dto.DirectoriesDto;
import com.exception.ServiceException;
import com.persistence.model.ContentFileModel;
import com.persistence.model.FileTypeModel;
import com.persistence.model.RootFolderModel;
import com.persistence.model.UserModel;
import com.service.ContentFileService;
import com.service.FileTypeService;
import com.service.RootFolderService;
import com.service.UserService;
import com.util.FileMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FolderFacade {
    private RootFolderService rootFolderService;

    private ContentFileService contentFileService;

    private FileMapper fileMapper;

    private UserService userService;

    private FileTypeService fileTypeService;

    public DirectoriesDto getDirectories(String uuid) {
        List<RootFolderModel> rootFolders = getRootFolders(uuid);
        DirectoriesDto directoriesDto = fileMapper.mapRootFolders(rootFolders);
        return directoriesDto;
    }

    private List<RootFolderModel> getRootFolders(String uuid) {
        UserModel userModel = userService.getUserByUuid(uuid);
        return rootFolderService.getAllRootFoldersByUserUuid(userModel);
    }

    public List<ContentFileDto> getAllFiles(String uuid) {
        List<ContentFileModel> contentFiles;
        try {
            contentFiles = rootFolderService.getRootFolderByUuid(uuid).getFiles();
        } catch (ServiceException exception) {
            contentFiles = contentFileService.findContentFileModelByUuid(uuid).getSubFiles();
        }
        return contentFiles
                .stream()
                .map(contentFileModel -> fileMapper.mapContentFileToFileMetadataDto(contentFileModel))
                .collect(Collectors.toList());
    }


    public void createSalut(String uuid) {
        FileTypeModel fileTypeModel = new FileTypeModel();
        fileTypeModel.setTypeName("directory");
        fileTypeModel.setIsActive(true);
        fileTypeService.save(fileTypeModel);
        UserModel userModel = userService.getUserByUuid(uuid);
        ContentFileModel contentFileModel = new ContentFileModel();
        contentFileModel.setFileName("salut");
        contentFileModel.setUuid(UUID.randomUUID().toString());
        contentFileModel.setFileCreator(userModel);
        contentFileModel.setAddedDate(ZonedDateTime.now());
        contentFileModel.setLastModifiedDate(ZonedDateTime.now());
        contentFileModel.setPath("/marian24/private/salut");
        RootFolderModel rootFolderModel = rootFolderService.getAllRootFoldersByUserUuid(userModel).get(0);
        contentFileModel.setRootFolder(rootFolderModel);
        contentFileModel.setFileTypeModel(fileTypeModel);
        contentFileModel = contentFileService.save(contentFileModel);
        List<ContentFileModel> contentFileModels = new ArrayList<>();
        contentFileModels.add(contentFileModel);
        rootFolderModel.setFiles(contentFileModels);
        rootFolderService.saveRootFolder(rootFolderModel);
        createServusInSalut(uuid, fileTypeModel, contentFileModel);
    }

    private void createServusInSalut(String uuid, FileTypeModel fileTypeModel, ContentFileModel contentFileModelParent) {
        UserModel userModel = userService.getUserByUuid(uuid);
        ContentFileModel contentFileModel = new ContentFileModel();
        contentFileModel.setFileName("servus");
        contentFileModel.setUuid(UUID.randomUUID().toString());
        contentFileModel.setFileCreator(userModel);
        contentFileModel.setAddedDate(ZonedDateTime.now());
        contentFileModel.setLastModifiedDate(ZonedDateTime.now());
        contentFileModel.setParentFolder(contentFileModelParent);
        contentFileModel.setPath("/marian24/private/salut/servus");
        RootFolderModel rootFolderModel = rootFolderService.getAllRootFoldersByUserUuid(userModel).get(0);
        contentFileModel.setRootFolder(rootFolderModel);
        contentFileModel.setFileTypeModel(fileTypeModel);
        List<ContentFileModel> contentFileModels = new ArrayList<>();
        contentFileModels.add(contentFileModel);
        contentFileModelParent.setSubFiles(contentFileModels);
        contentFileService.save(contentFileModel);
        contentFileService.save(contentFileModelParent);
    }
}
