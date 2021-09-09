package com.facade;

import com.controller.dto.*;
import com.exception.ServiceException;
import com.foldermanipulation.FolderCreator;
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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FolderFacade {
    private RootFolderService rootFolderService;

    private ContentFileService contentFileService;

    private FileMapper fileMapper;

    private UserService userService;

    private FileTypeService fileTypeService;

    private FolderCreator folderCreator;

    private ModelMapper modelMapper;

    public DirectoriesDto getDirectories(String uuid) {
        List<RootFolderModel> rootFolders = getRootFolders(uuid);
        DirectoriesDto directoriesDto = fileMapper.mapRootFolders(rootFolders);
        return directoriesDto;
    }

    private List<RootFolderModel> getRootFolders(String uuid) {
        UserModel userModel = userService.getUserByUuid(uuid);
        return rootFolderService.getAllRootFoldersByUserUuid(userModel);
    }

    public ContentDto getAllFiles(String uuid) {
        FileMetadataDto contentFileParentDto;
        List<ContentFileModel> contentFiles;
        try {
            RootFolderModel rootFolderModel = rootFolderService.getRootFolderByUuid(uuid);
            contentFiles = rootFolderModel.getFiles();
            contentFileParentDto = modelMapper.map(rootFolderModel, FileMetadataDto.class);
            contentFileParentDto.setFileCreator(rootFolderModel.getFolderCreator().getUsername());
        } catch (ServiceException exception) {
            ContentFileModel contentFileModel = contentFileService.findContentFileModelByUuid(uuid);
            contentFiles = contentFileModel.getSubFiles();
            contentFileParentDto = modelMapper.map(contentFileModel, FileMetadataDto.class);
            contentFileParentDto.setFileCreator(contentFileModel.getFileCreator().getUsername());
            if (contentFileModel.getParentFolder() == null) {
                contentFileParentDto.setParentUuid(contentFileModel.getRootFolder().getUuid());
            } else {
                contentFileParentDto.setParentUuid(contentFileModel.getParentFolder().getUuid());
            }
        }
        List<FileMetadataDto> contentFileDtos = contentFiles
                .stream()
                .map(contentFileModel -> fileMapper.mapContentFileToFileMetadataDto(contentFileModel))
                .collect(Collectors.toList());
        return new ContentDto(contentFileParentDto, contentFileDtos);
    }

    public DirectoryDto createFolder(CreateFolderDto createFolderDto, String username) {
        ContentFileModel parentFolder;
        UserModel creator = userService.findUserByUsername(username);
        ContentFileModel createdFolder = new ContentFileModel();
        createdFolder.setFileName(createFolderDto.getFolderName());
        createdFolder.setFileCreator(creator);
        createdFolder.setAddedDate(ZonedDateTime.now());
        createdFolder.setSubFiles(Collections.emptyList());
        createdFolder.setUuid(UUID.randomUUID().toString());
        createdFolder.setLastModifiedDate(ZonedDateTime.now());
        FileTypeModel fileTypeModel = fileTypeService.getFileTypeByName("directory");
        createdFolder.setFileTypeModel(fileTypeModel);
        try {
            parentFolder = contentFileService.getFileByUuid(createFolderDto.getFolderId());
            createdFolder.setParentFolder(parentFolder);
            createdFolder.setPath(parentFolder.getPath() + "/" + createFolderDto.getFolderName());
            createdFolder.setRootFolder(parentFolder.getRootFolder());
            parentFolder.getSubFiles().add(createdFolder);
            folderCreator.createFolder(createdFolder.getPath());
            contentFileService.save(createdFolder);
            contentFileService.save(parentFolder);
        } catch (ServiceException ex) {
            RootFolderModel rootFolder = rootFolderService.getRootFolderByUuid(createFolderDto.getFolderId());
            createdFolder.setPath(rootFolder.getPath() + "/" + createFolderDto.getFolderName());
            createdFolder.setRootFolder(rootFolder);
            createdFolder.setParentFolder(null);
            rootFolder.getFiles().add(createdFolder);
            folderCreator.createFolder(createdFolder.getPath());
            contentFileService.save(createdFolder);
            rootFolderService.saveRootFolder(rootFolder);
        }
        return fileMapper.mapContentFileToDirectoryDto(createdFolder);
    }

    public DirectoryDto renameFolder(RenameFolderDto renameFolderDto) {
        ContentFileModel folderToRename = contentFileService.findContentFileModelByUuid(renameFolderDto.getFolderId());
        //set subfolders path
        folderToRename.setFileName(renameFolderDto.getFolderName());
        String[] path = folderToRename.getPath().split("/");
        renamePaths(folderToRename.getSubFiles(), renameFolderDto.getFolderName(), path.length - 1);

        //set folder path
        path[path.length - 1] = renameFolderDto.getFolderName();
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(path).forEach(s -> stringBuilder.append(s).append("/"));
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        folderToRename.setPath(stringBuilder.toString());

        System.out.println("RENAMED FOLDER PATH : " + folderToRename.getPath());
        contentFileService.save(folderToRename);
        return fileMapper.mapContentFileToDirectoryDto(folderToRename);
    }

    public void renamePaths(List<ContentFileModel> contentFileModels, String name, int index){
        contentFileModels.forEach(contentFileModel ->
        {
            String[] pathToRename = contentFileModel.getPath().split("/");
            pathToRename[index] = name;
            StringBuilder stringBuilder = new StringBuilder();
            Arrays.stream(pathToRename).forEach(s -> stringBuilder.append(s).append("/"));
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            contentFileModel.setPath(stringBuilder.toString());
            renamePaths(contentFileModel.getSubFiles(), name, index);
        });

        System.out.println("PRINTING SUBFOLDERS");
        contentFileModels.forEach(c -> {
            System.out.println(c.getPath());
            printPaths(c.getSubFiles());
        });
    }

    public void printPaths(List<ContentFileModel> contentFileModels){
        contentFileModels.forEach(c -> System.out.println(c.getPath()));
    }
}
