package com.foldermanipulation;

import com.exception.FolderException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Component
public class FileService {

    @Value("${user.files}")
    private String BASIC_PATH;

    public void uploadFile(MultipartFile file, String path) {
        path = BASIC_PATH + path;
        InputStream in = getInputStream(file);
        try {
            OutputStream out = new FileOutputStream(path);
            IOUtils.copy(in, out);
            in.close();
            out.close();
        } catch (IOException e) {
            throw new FolderException("Bad file or something...");
        }
    }

    private InputStream getInputStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (IOException e) {
            throw new FolderException("Could not upload file");
        }
    }
}
