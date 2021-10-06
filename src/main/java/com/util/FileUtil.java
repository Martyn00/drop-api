package com.util;

import com.exception.FolderException;
import com.persistence.model.ContentFileModel;
import com.persistence.model.RootFolderModel;
import org.springframework.stereotype.Component;

@Component
public class FileUtil {
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
}
