package com.foldermanipulation;

import com.exception.FolderException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class FileService {

    @Value("${user.files}")
    private String BASIC_PATH;

    private static final int BYTE_RANGE = 512;

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

    public void moveFile(String initialPath, String movePath) {
        File fileToMove = new File(initialPath);
        boolean isMoved = fileToMove.renameTo(new File(movePath));
        if (!isMoved) {
            throw new FolderException("Folder or file cannot be moved!");
        }
    }

    public void copyFile(String initialPath, String movePath) {
        Path copied = Paths.get(movePath);
        Path originalPath = Paths.get(initialPath);
        try {
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FolderException("Folder or file cannot be copied");
        }
    }

    public byte[] readByteRange(String location, long start, long end) throws IOException {
        Path path = Paths.get(location);
        try (InputStream inputStream = (Files.newInputStream(path));
             ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream()) {
            byte[] data = new byte[BYTE_RANGE];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                bufferedOutputStream.write(data, 0, nRead);
            }
            bufferedOutputStream.flush();
            byte[] result = new byte[(int) (end - start) + 1];
            System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, result.length);
            return result;
        }
    }

    public Long sizeFromFile(Path path) {
        try {
            return Files.size(path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0L;
    }
}
