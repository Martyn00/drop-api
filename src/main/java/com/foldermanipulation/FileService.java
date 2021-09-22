package com.foldermanipulation;

import com.exception.FolderException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Component
public class FileService {
    public void uploadFile(InputStream in, String path) {
        path = "../server" + path;
        try {
            OutputStream out = new FileOutputStream(path);
            IOUtils.copy(in, out);
            in.close();
            out.close();
        } catch (IOException e) {
            throw new FolderException("Bad file or something...");
        }
    }
}
