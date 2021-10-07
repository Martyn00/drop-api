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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@AllArgsConstructor
public class FolderFacade {
    public static final String SLASH = "/";
    private final RootFolderService rootFolderService;

    private final ContentFileService contentFileService;

    private final FileMapper fileMapper;

    private final UserService userService;

    private final FileTypeService fileTypeService;

    private final FolderCreator folderCreator;

    private final ModelMapper modelMapper;

    private final UserFacade userFacade;

    private final FileFacade fileFacade;

    public DirectoriesDto getDirectories(String uuid) {
        List<RootFolderModel> rootFolders = userFacade.getAllRootFoldersByUserUuid(uuid);
        DirectoriesDto directoriesDto = fileMapper.mapRootFolders(rootFolders);
        return directoriesDto;
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
            System.out.println("RASAMATI");
            parentFolder = contentFileService.getFileByUuid(createFolderDto.getFolderId());
            fileFacade.checkUniqueName(parentFolder, createFolderDto.getFolderName());
            createdFolder.setParentFolder(parentFolder);
            createdFolder.setPath(parentFolder.getPath() + SLASH + createFolderDto.getFolderName());
            createdFolder.setRootFolder(parentFolder.getRootFolder());
            parentFolder.getSubFiles().add(createdFolder);
            folderCreator.createFolder(createdFolder.getPath());
            contentFileService.save(createdFolder);
            contentFileService.save(parentFolder);
        } catch (ServiceException ex) {
            RootFolderModel rootFolder = rootFolderService.getRootFolderByUuid(createFolderDto.getFolderId());
            fileFacade.checkUniqueName(rootFolder, createFolderDto.getFolderName());
            createdFolder.setPath(rootFolder.getPath() + SLASH + createFolderDto.getFolderName());
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
        folderToRename.setFileName(renameFolderDto.getFolderName());
        //set subfolders path
        String oldPath = folderToRename.getPath();
        String[] path = oldPath.split(SLASH);
        renamePaths(folderToRename.getSubFiles(), renameFolderDto.getFolderName(), path.length - 1);

        //set folder path
        StringBuilder newPath = createNewPath(path, path.length - 1, renameFolderDto.getFolderName());
        folderToRename.setPath(newPath.toString());
        folderCreator.renameFolder(oldPath, newPath.toString());
        folderToRename.setLastModifiedDate(ZonedDateTime.now());
        contentFileService.save(folderToRename);
        return fileMapper.mapContentFileToDirectoryDto(folderToRename);
    }

    public void renamePaths(List<ContentFileModel> contentFileModels, String name, int index) {
        contentFileModels.forEach(contentFileModel ->
        {
            String[] pathToRename = contentFileModel.getPath().split(SLASH);
            StringBuilder stringBuilder = createNewPath(pathToRename, index, name);
            contentFileModel.setPath(stringBuilder.toString());
            renamePaths(contentFileModel.getSubFiles(), name, index);
        });

        contentFileModels.forEach(c -> {
            System.out.println(c.getPath());
            printPaths(c.getSubFiles());
        });
    }

    public void deleteFileByUuid(String uuid) {
        ContentFileModel fileToDelete = contentFileService.findContentFileModelByUuid(uuid);
        ContentFileModel parent = fileToDelete.getParentFolder();
        if (parent != null) {
            parent.getSubFiles().remove(fileToDelete);
            contentFileService.save(parent);
        } else {
            RootFolderModel rootFolderModel = fileToDelete.getRootFolder();
            rootFolderModel.getFiles().remove(fileToDelete);
            rootFolderService.saveRootFolder(rootFolderModel);
        }
        contentFileService.deleteFileByUuid(uuid);
        folderCreator.deleteFolder(fileToDelete.getPath());
    }

    public void createTempDirectoryForUser(String user){
        File workingFile = new File(System.getProperty("user.dir"));
        File aboveWorking = new File(workingFile.getParent());
        try{
            Files.createDirectories(Paths.get(aboveWorking.getPath() + "/server/temp/" + user));
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public File zipDirectory(String path) throws IOException {
        File directoryToZip = new File(path);
        String[] splitPath = path.split("\\\\");
        String fileName = splitPath[splitPath.length - 1];
        String zipPath = "../server/temp/".concat(SecurityContextHolder.getContext().getAuthentication().getName())
                .concat(SLASH).concat(fileName).concat(".zip");
        FileOutputStream fileOutputStream = new FileOutputStream(zipPath);
        ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
        File[] files = directoryToZip.listFiles();
        for (File file : files) {
            FileInputStream fileInputStream = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOutputStream.putNextEntry(zipEntry);
            int length;
            byte[] bytes = new byte[1024];
            while ((length = fileInputStream.read(bytes)) >= 0) {
                zipOutputStream.write(bytes, 0, length);
            }
            fileInputStream.close();
        }

//        fileInputStream.close();
        zipOutputStream.close();
        fileOutputStream.close();
        return new File(zipPath);
    }

    private StringBuilder createNewPath(String[] splitPath, int folderToRenameIndex, String folderName) {
        splitPath[folderToRenameIndex] = folderName;
        StringBuilder newPath = new StringBuilder();
        Arrays.stream(splitPath).forEach(s -> newPath.append(s).append(SLASH));
        newPath.deleteCharAt(newPath.length() - 1);
        return newPath;
    }

    public void printPaths(List<ContentFileModel> contentFileModels) {
        contentFileModels.forEach(c -> System.out.println(c.getPath()));
    }

    public Boolean checkFileExistsByName(String parentUuid, String fileName) {
        return contentFileService.checkFileExistsByName(parentUuid, fileName);
    }
}
