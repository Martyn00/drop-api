package com.controller;

import com.controller.dto.*;
import com.facade.FileFacade;
import com.facade.FolderFacade;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping(value = "/folders")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
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
    public ResponseEntity<String> uploadFile(@RequestParam("files") MultipartFile files, @PathVariable String parentUuid) {
        fileFacade.uploadFile(files, files.getOriginalFilename(), parentUuid);
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

    @GetMapping(value = "/{fileUuid}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Transactional(timeout = 3000)
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileUuid, HttpServletRequest request, HttpServletResponse response) throws IOException {
        File fileToDownload = fileFacade.getFile(fileUuid);
        Resource resource = fileFacade.download(fileToDownload);
        System.out.println(fileToDownload.exists());
        WebClient webClient = WebClient.create(fileToDownload.toURI().toURL().toString());
        System.out.println(request.getRequestURL().toString());

//        Flux<DataBuffer> readFileToDownload = DataBufferUtils.read(fileToDownload.toPath(), new DefaultDataBufferFactory(), 2048);
        String token = request.getHeader("Authorization").split(" ")[1];
        System.out.println(token);
//        readFileToDownload.subscribe(System.out::println);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(resource.getFile().toPath()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
//        return readFileToDownload;

//        Flux<DataBuffer> dataBuffer = webClient
//                .mutate()
//                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
//                .clientConnector(new ReactorClientHttpConnector((HttpClient.create(ConnectionProvider.newConnection()))))
//                .build()
//                .get()
//                .accept(MediaType.ALL, MediaType.APPLICATION_OCTET_STREAM)
//                .retrieve()
//                .bodyToFlux(DataBuffer.class);

//        DataBufferUtils.write(readFileToDownload, Path.of("Z:\\bigfile\\10gb.test"), StandardOpenOption.CREATE).block();
//        File file = fileFacade.getFile(fileUuid);
//        if (file.exists()) {
//
//            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
//            if (mimeType == null) {
//                mimeType = "application/octet-stream";
//            }
//
//            response.setContentType(mimeType);
//
//            response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));
//
//            response.setContentLengthLong(file.length());
//
//            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
//
//            FileCopyUtils.copy(inputStream, response.getOutputStream());

//        }
    }
}



