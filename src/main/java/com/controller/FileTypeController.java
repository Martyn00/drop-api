package com.controller;

import com.controller.dto.FileTypeDto;
import com.facade.FileTypeFacade;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/fileTypes")
@AllArgsConstructor
public class FileTypeController {
    private FileTypeFacade fileTypeFacade;

    @GetMapping
    public ResponseEntity<List<FileTypeDto>> getAllFileTypes() {
        return new ResponseEntity<>(fileTypeFacade.getAllFileTypes(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<FileTypeDto> AddFileType(@RequestParam("type") String type) {
        return new ResponseEntity<>(fileTypeFacade.addFileType(type), HttpStatus.CREATED);
    }
}
