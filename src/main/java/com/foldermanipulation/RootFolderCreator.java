package com.foldermanipulation;

import com.exception.FolderException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class RootFolderCreator {
    public static final String SLASH = "/";
    @Value("${user.files}")
    private String BASIC_PATH;

    public void createServerDirectory() {
        File workingFile = new File(System.getProperty("user.dir"));
        File aboveWorking = new File(workingFile.getParent());
        try {
            Files.createDirectories(Paths.get(aboveWorking.getPath().concat("/server")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createTempDirectory(){
        File workingFile = new File(System.getProperty("user.dir"));
        File aboveWorking = new File(workingFile.getParent());
        try{
            Files.createDirectories(Paths.get(aboveWorking.getPath().concat("/server/temp")));
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void createDirectoryAboveWorkingDirectory(String path){
        File workingFile = new File(System.getProperty("user.dir"));
        File aboveWorking = new File(workingFile.getParent());
        try{
            Files.createDirectories(Paths.get(aboveWorking.getPath().concat(path)));
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void createBasicUserFolders(String username) {
        File usernameDirectory = new File(BASIC_PATH.concat(SLASH).concat(username));
        checkFolderExists(username, usernameDirectory);
        usernameDirectory.mkdir();
        createPrivateDirectory(username);
    }

    public void createRootFolder(String path) {
        File rootFolder = new File(BASIC_PATH.concat(SLASH).concat(path));
        checkFolderExists(rootFolder.getName(), rootFolder);
        rootFolder.mkdir();
    }

    private void createPrivateDirectory(String username) {
        File privateDirectory = new File(BASIC_PATH.concat(SLASH).concat(username).concat(SLASH).concat("My drive"));
        checkFolderExists("My drive", privateDirectory);
        privateDirectory.mkdir();
    }

    private void checkFolderExists(String folderName, File usernameDirectory) {
        if (usernameDirectory.exists()) {
            throw new FolderException(folderName + " folder already exists");
        }
    }
}
