package com.foldermanipulation;

import com.exception.FolderException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
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

    private void checkFolderExists(String folderName, File usernameDirectory) {
        if (usernameDirectory.exists()) {
            throw new FolderException(MessageFormat.format("Folder {0} already exists", folderName));
        }
    }
}