package com.controller;

import com.controller.dto.MediaDto;
import com.facade.MediaFacade;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@RestController(value = "media")
@AllArgsConstructor
public class MediaController {
    public static final String VIDEO = "video";
    public static final String IMAGE = "image";
    private final MediaFacade mediaFacade;

    @GetMapping(value = "/image/{fileUuid}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String fileUuid) throws FileNotFoundException {
        MediaDto imageDto = mediaFacade.getMediaDto(fileUuid, IMAGE);
        File image = new File(imageDto.getPath());
        InputStream inputStream = new FileInputStream(image);
        return ResponseEntity
                .ok()
                .contentType(imageDto.getMediaType())
                .body(new InputStreamResource(inputStream));
    }

    @GetMapping(value = "/video/{fileUuid}")
    public ResponseEntity<InputStreamResource> getVideo(@PathVariable String fileUuid) throws FileNotFoundException {
        MediaDto videoDto = mediaFacade.getMediaDto(fileUuid, VIDEO);
        File video = new File(videoDto.getPath());
        InputStream inputStream = new FileInputStream(video);
        return ResponseEntity
                .ok()
                .contentType(videoDto.getMediaType())
                .body(new InputStreamResource(inputStream));
    }
}
