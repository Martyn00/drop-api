package com.foldermanipulation;

import com.exception.FolderException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class RootFolderCreator {
    public static final String SLASH = "/";
    @Value("${user.files}")
    private String BASIC_PATH;

    public void createRootFolders(String username) {
        File usernameDirectory = new File(BASIC_PATH.concat(SLASH).concat(username));
        checkFolderExists(username, usernameDirectory);
        usernameDirectory.mkdir();
        createPrivateDirectory(username);
        createSharedDirectory(username);
    }

    private void createPrivateDirectory(String username) {
        File privateDirectory = new File(BASIC_PATH.concat(SLASH).concat(username).concat(SLASH).concat("private"));
        checkFolderExists("private", privateDirectory);
        privateDirectory.mkdir();
    }

    private void createSharedDirectory(String username) {
        File sharedDirectory = new File(BASIC_PATH.concat(SLASH).concat(username).concat(SLASH).concat("shared"));
        checkFolderExists("shared", sharedDirectory);
        sharedDirectory.mkdir();
    }

    private void checkFolderExists(String folderName, File usernameDirectory) {
        if (usernameDirectory.exists()) {
            throw new FolderException(folderName + " folder already exists");
        }
    }
}
