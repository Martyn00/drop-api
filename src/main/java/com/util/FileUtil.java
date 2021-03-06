package com.util;

import com.exception.FolderException;
import com.persistence.model.ContentFileModel;
import com.persistence.model.FileTypeModel;
import com.persistence.model.RootFolderModel;
import com.service.FileTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FileUtil {

    public static final int FIRST_ELEMENT = 2;
    FileTypeService fileTypeService;

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

    public ContentFileModel setBasicData(String fileName, double size, String fileMime) {
        ContentFileModel contentFileModel = new ContentFileModel();
        contentFileModel.setUuid(UUID.randomUUID().toString());
        contentFileModel.setAddedDate(ZonedDateTime.now());
        contentFileModel.setLastModifiedDate(ZonedDateTime.now());
        contentFileModel.setSize(size);
        contentFileModel.setFileName(fileName);
        contentFileModel.setFileMime(fileMime);
        contentFileModel.setFileTypeModel(fileTypeService.findFileTypeByFileMime(fileMime));
        return contentFileModel;
    }

    public ContentFileModel setBasicData(String fileName, double size, FileTypeModel fileTypeModel) {
        ContentFileModel contentFileModel = new ContentFileModel();
        contentFileModel.setUuid(UUID.randomUUID().toString());
        contentFileModel.setAddedDate(ZonedDateTime.now());
        contentFileModel.setLastModifiedDate(ZonedDateTime.now());
        contentFileModel.setSize(size);
        contentFileModel.setFileName(fileName);
        contentFileModel.setFileMime(fileTypeModel.getTypeName());
        contentFileModel.setFileTypeModel(fileTypeModel);
        return contentFileModel;
    }

    public FileTypeModel getFileType(String fileTypeName) {
        return fileTypeService.getAllFileTypes().stream().
                filter(fileType -> fileType.getTypeName()
                        .equals(fileTypeName)).findFirst().get();
    }

    public String changePath(String path) {
        String[] pathParts = path.split("/");
        String[] modifiedPathParts = Arrays.copyOfRange(pathParts, FIRST_ELEMENT, pathParts.length);
        return Arrays.stream(modifiedPathParts).map(pathPart -> "/" + pathPart).collect(Collectors.joining());
    }
}
