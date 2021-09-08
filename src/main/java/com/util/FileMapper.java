package com.util;

import com.controller.dto.ContentFileDto;
import com.controller.dto.DirectoriesDto;
import com.controller.dto.DirectoryDto;
import com.persistence.model.ContentFileModel;
import com.persistence.model.RootFolderModel;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FileMapper {
    public static final String DIRECTORY = "directory";
    private final ModelMapper modelMapper;
    public DirectoriesDto mapRootFolders(List<RootFolderModel> rootFolders) {
        DirectoriesDto directoriesDto = new DirectoriesDto();
        directoriesDto.setDirectories(rootFolders.stream().map(this::mapRootFolderToDirectoryDto).collect(Collectors.toList()));
        return directoriesDto;
    }

    private DirectoryDto mapRootFolderToDirectoryDto(RootFolderModel rootFolder) {
        DirectoryDto directoryDto = new DirectoryDto();
        directoryDto.setFileName(rootFolder.getFolderName());
        directoryDto.setUuid(rootFolder.getUuid());
        System.out.println(rootFolder.getFiles());
        directoryDto.setPath(rootFolder.getPath());
        List<ContentFileModel> files = rootFolder.getFiles()
                .stream()
                .filter(contentFileModel -> contentFileModel.getFileTypeModel().getTypeName().equals(DIRECTORY))
                .collect(Collectors.toList());
        directoryDto.setSubfolders(files.stream().map(this::mapContentFileToDirectoryDto).collect(Collectors.toList()));
        return directoryDto;
    }

    private DirectoryDto mapContentFileToDirectoryDto(ContentFileModel contentFile) {
        DirectoryDto directoryDto = new DirectoryDto();
        directoryDto.setFileName(contentFile.getFileName());
        directoryDto.setUuid(contentFile.getUuid());
        List<ContentFileModel> folders = contentFile.getSubFiles()
                .stream()
                .filter(contentFileModel -> contentFileModel.getFileTypeModel().getTypeName().equals(DIRECTORY))
                .collect(Collectors.toList());
        directoryDto.setSubfolders(folders
                .stream()
                .map(this::mapContentFileToDirectoryDto)
                .collect(Collectors.toList()));
        directoryDto.setPath(contentFile.getPath());
        if (contentFile.getParentFolder() == null) {
            directoryDto.setParentUuid("");
        } else {
            directoryDto.setParentUuid(contentFile.getParentFolder().getUuid());
        }
        return directoryDto;
    }

    public ContentFileDto mapContentFileToFileMetadataDto(ContentFileModel contentFileModel) {
        ContentFileDto fileMetadataDto = modelMapper.map(contentFileModel, ContentFileDto.class);
        fileMetadataDto.setFileCreator(contentFileModel.getFileCreator().getUsername());
        return fileMetadataDto;
    }
}
