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
import com.util.FileUtil;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Component
@AllArgsConstructor
public class FolderFacade {
    public static final String SLASH = "/";
    public static final String SERVER_DIR = "server";
    public static final String TEMP_DIR = "temp";
    public static final String PARENT_DIRECTORY = "..";
    public static final String ZIP = ".zip";
    public static final String DIRECTORY_FILE_TYPE = "directory";
    private final RootFolderService rootFolderService;

    private final ContentFileService contentFileService;

    private final FileMapper fileMapper;

    private final UserService userService;

    private final FileTypeService fileTypeService;

    private final FolderCreator folderCreator;

    private final ModelMapper modelMapper;

    private final UserFacade userFacade;

    private final FileUtil fileutil;

    public DirectoriesDto getDirectories(String uuid) {
        List<RootFolderModel> rootFolders = userFacade.getAllRootFoldersByUserUuid(uuid);
//        changePaths here
        rootFolders.forEach(rootFolderModel -> rootFolderModel
                .setPath(fileutil.changePath(rootFolderModel.getPath())));
        return fileMapper.mapRootFolders(rootFolders);
    }

    public ContentDto getAllFiles(String uuid) {
        FileMetadataDto contentFileParentDto;
        List<ContentFileModel> contentFiles;
        try {
            RootFolderModel rootFolderModel = rootFolderService.getRootFolderByUuid(uuid);
            contentFiles = rootFolderModel.getFiles();
            contentFileParentDto = modelMapper.map(rootFolderModel, FileMetadataDto.class);
            contentFileParentDto.setFileCreator(rootFolderModel.getFolderCreator().getUsername());
            contentFileParentDto.setIsShared(rootFolderModel.getShared());
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
            contentFileParentDto.setIsShared(false);
        }
        List<FileMetadataDto> contentFileDtos = contentFiles
                .stream()
                .map(fileMapper::mapContentFileToFileMetadataDto)
                .collect(Collectors.toList());
        contentFileDtos.forEach(contentFile -> contentFile.setPath(fileutil.changePath(contentFile.getPath())));
        contentFileDtos.forEach(contentFile -> contentFile.setIsShared(false));
        contentFileParentDto.setPath(fileutil.changePath(contentFileParentDto.getPath()));
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
        FileTypeModel fileTypeModel = fileTypeService.getFileTypeByName(DIRECTORY_FILE_TYPE);
        createdFolder.setFileTypeModel(fileTypeModel);
        try {
            parentFolder = contentFileService.getFileByUuid(createFolderDto.getFolderId());
            fileutil.checkUniqueName(parentFolder, createFolderDto.getFolderName());
            createdFolder.setParentFolder(parentFolder);
            createdFolder.setPath(parentFolder.getPath() + SLASH + createFolderDto.getFolderName());
            createdFolder.setRootFolder(parentFolder.getRootFolder());
            parentFolder.getSubFiles().add(createdFolder);
            folderCreator.createFolder(createdFolder.getPath());
            contentFileService.save(createdFolder);
        } catch (ServiceException ex) {
            RootFolderModel rootFolder = rootFolderService.getRootFolderByUuid(createFolderDto.getFolderId());
            fileutil.checkUniqueName(rootFolder, createFolderDto.getFolderName());
            createdFolder.setPath(rootFolder.getPath() + SLASH + createFolderDto.getFolderName());
            createdFolder.setRootFolder(rootFolder);
            createdFolder.setParentFolder(null);
            rootFolder.getFiles().add(createdFolder);
            folderCreator.createFolder(createdFolder.getPath());
            contentFileService.save(createdFolder);
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
            rootFolderService.save(rootFolderModel);
        }
        contentFileService.deleteFileByUuid(uuid);
        folderCreator.deleteFolder(fileToDelete.getPath());
    }

    public void createTempDirectoryForUser(String user) {
        File workingFile = new File(System.getProperty("user.dir"));
        File aboveWorking = new File(workingFile.getParent());
        try {
            Files.createDirectories(Paths.get(aboveWorking.getPath() + SLASH + SERVER_DIR + SLASH + TEMP_DIR + SLASH + user));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File zipAll(String path, String directoryName) {
        File directoryToZip = new File(path);
        String zipPath = (PARENT_DIRECTORY + SLASH + SERVER_DIR + SLASH + TEMP_DIR + SLASH).
                concat(SecurityContextHolder.getContext().getAuthentication().getName())
                .concat(SLASH).concat(directoryName).concat(ZIP);
        File newZip = new File(zipPath);
        ZipUtil.pack(directoryToZip, newZip);
        return newZip;
    }

    public Boolean checkFileExistsByName(String parentUuid, String fileName) {
        return contentFileService.checkFileExistsByName(parentUuid, fileName);
    }

    public List<FileMetadataDto> searchFolder(String folderUuid, String fileName, String fileType, SearchRangeDto searchRangeDto) {
        if (searchRangeDto.equals(SearchRangeDto.ALL)) {
            System.out.println("ALL");
        }
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel userModel = userService.findUserByUsername(user);
        List<FileMetadataDto> fileMetadataDtos = new ArrayList<>();

        if (searchRangeDto.equals(SearchRangeDto.ME)) {
            searchPrivateDrive(folderUuid, fileName, fileType, userModel, fileMetadataDtos);
        } else if (searchRangeDto.equals(SearchRangeDto.OTHERS)) {
            searchSharedDrives(folderUuid, fileName, fileType, userModel, fileMetadataDtos);
        } else {
            List<RootFolderModel> accessibleRootFolders = userModel.getAccessibleRootFolders();
            searchInSubFolder(fileName, fileType, accessibleRootFolders, fileMetadataDtos);
        }
        return fileMetadataDtos;
    }

    public void deleteMultipleFiles(List<FileDeleteDto> foldersToDeleteUuid) {
        foldersToDeleteUuid.forEach(fileDeleteDto -> deleteFileByUuid(fileDeleteDto.getFileToDeleteUuid()));
    }

    private void searchSharedDrives(String folderUuid, String fileName, String fileType, UserModel userModel, List<FileMetadataDto> fileMetadataDtos) {
        try {
            searchInSubFolder(folderUuid, fileName, fileType, fileMetadataDtos);
        } catch (ServiceException serviceException) {
            List<RootFolderModel> sharedDrives = userModel.getAccessibleRootFolders()
                    .stream()
                    .filter(rootFolderModel -> rootFolderModel.getShared().equals(true)).collect(Collectors.toList());
            sharedDrives.forEach(sharedDrive -> findAllFiles(fileName, fileType, sharedDrive.getFiles(), fileMetadataDtos));
        }
    }

    private void searchPrivateDrive(String folderUuid, String fileName, String fileType, UserModel userModel, List<FileMetadataDto> fileMetadataDtos) {
        try {
            searchInSubFolder(folderUuid, fileName, fileType, fileMetadataDtos);
        } catch (ServiceException serviceException) {
            RootFolderModel myDrive = userModel.getAccessibleRootFolders()
                    .stream()
                    .filter(rootFolderModel -> rootFolderModel.getShared().equals(false)).findFirst().get();
            findAllFiles(fileName, fileType, myDrive.getFiles(), fileMetadataDtos);
        }
    }

    private void searchInSubFolder(String fileName, String fileType, List<RootFolderModel> rootFolderModels, List<FileMetadataDto> fileMetadataDtos) {
        rootFolderModels.forEach(rootFolderModel -> findAllFiles(fileName, fileType, rootFolderModel.getFiles(), fileMetadataDtos));
    }

    private void searchInSubFolder(String folderUuid, String fileName, String fileType, List<FileMetadataDto> fileMetadataDtos) {
        ContentFileModel contentFileModel = contentFileService.findContentFileModelByUuid(folderUuid);
        findAllFiles(fileName, fileType, contentFileModel.getSubFiles(), fileMetadataDtos);
    }

    private void findAllFiles(String fileName, String fileType, List<ContentFileModel> directoryDtos, List<FileMetadataDto> fileMetadataDtos) {
        if (fileType == null) {
            directoryDtos.forEach(fileModel -> {
                if (fileModel.getFileName().startsWith(fileName)) {
                    fileMetadataDtos.add(fileMapper.mapContentFileToFileMetadataDto(fileModel));
                }
                if (fileModel.getFileTypeModel().getTypeName().equals("directory")) {
                    findAllFiles(fileName, fileType, fileModel.getSubFiles(), fileMetadataDtos);
                }
            });
        } else {
            directoryDtos.forEach(fileModel -> {
                if (fileModel.getFileName().startsWith(fileName) && fileType.equals(fileModel.getFileTypeModel().getTypeName())) {
                    fileMetadataDtos.add(fileMapper.mapContentFileToFileMetadataDto(fileModel));
                }
                if (fileModel.getFileTypeModel().getTypeName().equals("directory")) {
                    findAllFiles(fileName, fileType, fileModel.getSubFiles(), fileMetadataDtos);
                }
            });
        }
    }

    private StringBuilder createNewPath(String[] splitPath, int folderToRenameIndex, String folderName) {
        splitPath[folderToRenameIndex] = folderName;
        StringBuilder newPath = new StringBuilder();
        Arrays.stream(splitPath).forEach(s -> newPath.append(s).append(SLASH));
        newPath.deleteCharAt(newPath.length() - 1);
        return newPath;
    }
    //METHOD BELOW COMMENTED AND KEPT FOR FURTHER DEVELOPMENT IF NEEDED
//    public File zipDirectory(String path) throws IOException {
//        File directoryToZip = new File(path);
//        String[] splitPath = path.split("\\\\");
//        String fileName = splitPath[splitPath.length - 1];
//        String zipPath = (PARENT_DIRECTORY + SLASH + SERVER_DIR + SLASH + TEMP_DIR + SLASH).
//                concat(SecurityContextHolder.getContext().getAuthentication().getName())
//                .concat(SLASH).concat(fileName).concat(ZIP);
//        try (FileOutputStream fileOutputStream = new FileOutputStream(zipPath);
//             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
//            File[] files = directoryToZip.listFiles();
//            Objects.requireNonNull(files);
//            for (File file : files) {
//                try (FileInputStream fileInputStream = new FileInputStream(file)) {
//                    ZipEntry zipEntry = new ZipEntry(file.getName());
//                    zipOutputStream.putNextEntry(zipEntry);
//                    int length;
//                    byte[] bytes = new byte[1024];
//                    while ((length = fileInputStream.read(bytes)) >= 0) {
//                        zipOutputStream.write(bytes, 0, length);
//                    }
//                }
//            }
//        }

//        return new File(zipPath);

//    }
}
