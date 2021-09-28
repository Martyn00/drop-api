package com.controller;

import com.controller.dto.*;
import com.facade.FileFacade;
import com.facade.FolderFacade;
import com.facade.RootFolderFacade;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/folders")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FolderController {

    private final FolderFacade folderFacade;

    private final FileFacade fileFacade;

    private final RootFolderFacade rootFolderFacade;

    @GetMapping(value = "/{uuid}")
    public ResponseEntity<DirectoriesDto> getAllDirectories(@PathVariable String uuid) {
        return new ResponseEntity<>(folderFacade.getDirectories(uuid), HttpStatus.OK);
    }

    @GetMapping("/content/{folderUuid}")
    public ResponseEntity<ContentDto> getAllContent(@PathVariable String folderUuid) {
        return new ResponseEntity<>(folderFacade.getAllFiles(folderUuid), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DirectoryDto> createDirectory(@RequestBody CreateFolderDto createFolderDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(folderFacade.
                createFolder(createFolderDto, username), HttpStatus.CREATED);
    }

    @PostMapping(value = "/file-upload/{parentUuid}", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadFile(@RequestParam("files") MultipartFile[] files, @PathVariable String parentUuid) {
        Arrays.stream(files).forEach(file -> fileFacade.uploadFile(file, file.getOriginalFilename(), parentUuid));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/create-shared-folder/{folderName}")
    public ResponseEntity<String> uploadFile(@PathVariable String folderName) {
        rootFolderFacade.createSharedFolder(folderName);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "add-users-to-shared-folder/{folderUuid}")
    public ResponseEntity<String> addUsersToSharedFolder(@PathVariable String folderUuid, @RequestBody List<AddedUserDto> addedUserDtos) {
        rootFolderFacade.addUsersToSharedFolder(folderUuid, addedUserDtos);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping
    public ResponseEntity<DirectoryDto> renameDirectory(@RequestBody RenameFolderDto renameFolderDto) {
        return new ResponseEntity<>(folderFacade.renameFolder(renameFolderDto), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{uuid}")
    public ResponseEntity<Object> deleteFile(@PathVariable String uuid) {
        folderFacade.deleteFileByUuid(uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/exists/{parentUuid}/{folderName}")
    public ResponseEntity<Boolean> checkFileExistsByName(@PathVariable String parentUuid, @PathVariable String folderName) {
        return new ResponseEntity<>(folderFacade.checkFileExistsByName(parentUuid, folderName), HttpStatus.OK);
    }

    @GetMapping(value = "/{fileUuid}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Transactional(timeout = 3000)
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable String fileUuid) throws IOException {
        File fileToDownload = fileFacade.getFile(fileUuid);
        System.out.println(fileToDownload.exists());
        FileSystemResource fileSystemResource = new FileSystemResource(fileToDownload);

        String mime = Files.probeContentType(fileToDownload.toPath());
        if (mime == null || mime.equals("text/plain") || mime.equals("image/png")) {
            mime = "application/octet-stream";
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, mime);
        responseHeaders.setContentLength(fileSystemResource.contentLength());
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileToDownload.getName());
        responseHeaders.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(fileSystemResource);
    }
}



