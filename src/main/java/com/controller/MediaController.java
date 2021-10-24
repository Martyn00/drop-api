package com.controller;

import com.controller.dto.MediaDto;
import com.facade.MediaFacade;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/media")
public class MediaController {
    public static final String AUDIO = "audio";
    private static final int BYTE_RANGE = 128; // increase the byterange from here

    public static final String VIDEO = "video";
    public static final String IMAGE = "image";

    private static final String BASIC_PATH = "../server";

    private final MediaFacade mediaFacade;

    @GetMapping(value = "/image/{fileUuid}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileUuid) throws IOException {
        MediaDto imageDto = mediaFacade.getMediaDto(fileUuid, IMAGE);
        File image = new File(imageDto.getPath());
//        InputStream inputStream = new FileInputStream(image);
//        return ResponseEntity
//                .ok()
//                .contentType(imageDto.getMediaType())
//                .body(new InputStreamResource(inputStream));
        final ByteArrayResource inputStream = new ByteArrayResource(Files.readAllBytes(Paths.get(image.getPath())));
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(inputStream.contentLength())
                .body(inputStream);
    }

    @GetMapping(value = "/video/{filePath}")
    public Mono<ResponseEntity<byte[]>> streamVideo(@RequestHeader(value = "Range", required = false) String httpRangeList,
                                                    @PathVariable String filePath) {
        //        can add find filePath from database
        return Mono.just(getContent(BASIC_PATH + filePath, httpRangeList, VIDEO));
    }

    @GetMapping("/audio/{filePath}")
    public Mono<ResponseEntity<byte[]>> streamAudio(@RequestHeader(value = "Range", required = false) String httpRangeList,
                                                    @PathVariable String filePath) {
        //        can add find filePath from database
        return Mono.just(getContent(BASIC_PATH + filePath, httpRangeList, AUDIO));
    }

    private ResponseEntity<byte[]> getContent(String location, String range, String contentTypePrefix) {
        long rangeStart = 0;
        long rangeEnd;
        byte[] data;
        Long fileSize;
        String fileType = location.substring(location.lastIndexOf(".") + 1);
        try {
            fileSize = Optional.ofNullable(location)
                    .map(file -> Paths.get(location))
                    .map(this::sizeFromFile)
                    .orElse(0L);
            if (range == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .header("Content-Type", contentTypePrefix + "/" + fileType)
                        .header("Content-Length", String.valueOf(fileSize))
                        .body(readByteRange(location, rangeStart, fileSize - 1));
            }
            String[] ranges = range.split("-");
            rangeStart = Long.parseLong(ranges[0].substring(6));
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = fileSize - 1;
            }
            if (fileSize < rangeEnd) {
                rangeEnd = fileSize - 1;
            }
            data = readByteRange(location, rangeStart, rangeEnd);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header("Content-Type", contentTypePrefix +
                        "/" + fileType)
                .header("Accept-Ranges", "bytes")
                .header("Content-Length", contentLength)
                .header("Content-Range", "bytes" + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                .body(data);
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

    private Long sizeFromFile(Path path) {
        try {
            return Files.size(path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0L;
    }
}
