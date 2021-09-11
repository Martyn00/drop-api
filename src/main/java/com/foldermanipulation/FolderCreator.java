package com.foldermanipulation;

import com.exception.FolderException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;

@Component
public class FolderCreator {

    @Value("${user.files}")
    private String BASIC_PATH;

    public void createFolder(String path) {
        File file = new File(BASIC_PATH + path);
        System.out.println(BASIC_PATH + path);
        String[] splitPath = path.split("/");
        String fileName = splitPath[splitPath.length - 1];
        checkFolderExists(fileName, file);
        file.mkdir();
    }

    public void renameFolder(String oldPath, String newPath) {
        try {
            Files.move(Path.of(BASIC_PATH + oldPath), Path.of(BASIC_PATH + newPath));
        } catch (IOException e) {
            throw new FolderException(MessageFormat.format("Could not rename folder at path {0} ", oldPath));
        }
    }

    private void checkFolderExists(String folderName, File usernameDirectory) {
        if (usernameDirectory.exists()) {
            throw new FolderException(MessageFormat.format("Folder {0} already exists", folderName));
        }
    }
}