package com.controller;

import com.controller.dto.ContentFileDto;
import com.controller.dto.DirectoriesDto;
import com.facade.FolderFacade;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/folders")
@AllArgsConstructor
public class FolderController {

    private final FolderFacade folderFacade;

    @GetMapping(value = "/{uuid}")
    public ResponseEntity<DirectoriesDto> getAllDirectories(@PathVariable String uuid) {
        return new ResponseEntity<>(folderFacade.getDirectories(uuid), HttpStatus.OK);
    }

    @PostMapping(value = "/salut/{uuid}")
    public ResponseEntity<DirectoriesDto> createSalut(@PathVariable String uuid) {
        folderFacade.createSalut(uuid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/content/{folderUuid}")
    public ResponseEntity<List<ContentFileDto>> getAllContent(@PathVariable String folderUuid) {
        return new ResponseEntity<>(folderFacade.getAllFiles(folderUuid), HttpStatus.OK);
    }
}
