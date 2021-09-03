package com.foldermanipulation;

import com.exception.FolderException;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class RootFolderCreator {
    private static final String BASIC_PATH = "../server/";

    public void createRootFolders(String username) {
        File usernameDirectory = new File(BASIC_PATH.concat(username));
        checkFolderExists(username, usernameDirectory);
        usernameDirectory.mkdir();
        createPrivateDirectory(username);
        createSharedDirectory(username);
    }

    private void createPrivateDirectory(String username) {
        File privateDirectory = new File(BASIC_PATH.concat(username).concat("/").concat("private"));
        checkFolderExists("private", privateDirectory);
        privateDirectory.mkdir();
    }

    private void createSharedDirectory(String username) {
        File sharedDirectory = new File(BASIC_PATH.concat(username).concat("/").concat("shared"));
        checkFolderExists("shared", sharedDirectory);
        sharedDirectory.mkdir();
    }

    private void checkFolderExists(String folderName, File usernameDirectory) {
        if (usernameDirectory.exists()) {
            throw new FolderException(folderName + " folder already exists");
        }
    }
}
