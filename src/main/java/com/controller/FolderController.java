package com.controller;

import com.controller.dto.*;
import com.facade.FileFacade;
import com.facade.FolderFacade;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@RestController
@RequestMapping(value = "/folders")
@AllArgsConstructor
public class FolderController {

    private final FolderFacade folderFacade;

    private final FileFacade fileFacade;

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

    @PutMapping
    public ResponseEntity<DirectoryDto> renameDirectory(@RequestBody RenameFolderDto renameFolderDto){
        return new ResponseEntity<>(folderFacade.renameFolder(renameFolderDto), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{uuid}")
    public ResponseEntity<Object> deleteFile(@PathVariable String uuid){
        folderFacade.deleteFileByUuid(uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/exists/{folderName}")
    public ResponseEntity<Boolean> checkFileExistsByName(@PathVariable String folderName){
        return new ResponseEntity<>(folderFacade.checkFileExistsByName(folderName), HttpStatus.OK);
    }
}
