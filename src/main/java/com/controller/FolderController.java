package com.controller;

import com.controller.dto.*;
import com.facade.FolderFacade;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/folders")
@AllArgsConstructor
public class FolderController {

    private final FolderFacade folderFacade;

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

    @PutMapping
    public ResponseEntity<DirectoryDto> renameDirectory(@RequestBody RenameFolderDto renameFolderDto){
        return new ResponseEntity<>(folderFacade.renameFolder(renameFolderDto), HttpStatus.OK);
    }
}
