package com.util;

import com.exception.FolderException;
import com.persistence.model.ContentFileModel;
import com.persistence.model.FileTypeModel;
import com.persistence.model.RootFolderModel;
import com.service.FileTypeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.UUID;

@Component
@AllArgsConstructor
public class FileUtil {

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

    public ContentFileModel setBasicData(String fileName, double size) {
        ContentFileModel contentFileModel = new ContentFileModel();
        contentFileModel.setUuid(UUID.randomUUID().toString());
        contentFileModel.setAddedDate(ZonedDateTime.now());
        contentFileModel.setLastModifiedDate(ZonedDateTime.now());
        contentFileModel.setSize(size);
        contentFileModel.setFileName(fileName);
        contentFileModel.setFileTypeModel(getFileType("text"));
        return contentFileModel;
    }

    public FileTypeModel getFileType(String fileTypeName) {
        return fileTypeService.getAllFileTypes().stream().
                filter(fileType -> fileType.getTypeName()
                        .equals(fileTypeName)).findFirst().get();
    }
}
