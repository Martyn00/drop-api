package com.facade;

import com.controller.dto.MediaDto;
import com.exception.MediaException;
import com.persistence.model.ContentFileModel;
import com.service.ContentFileService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MediaFacade {

    @Value("${user.files}")
    private static String BASIC_PATH;
    @Autowired
    private final ContentFileService contentFileService;

    public MediaDto getMediaDto(String fileUuid, String fileType) {
        ContentFileModel contentFileModel = contentFileService.getFileByUuid(fileUuid);
        if (!contentFileModel.getFileTypeModel().getTypeName().equals(fileType)) {
            throw new MediaException("The file you have chosen is not an image.");
        }
        MediaDto mediaDto = new MediaDto();
        mediaDto.setMediaType(MediaType.valueOf(contentFileModel.getFileMime()));
        mediaDto.setPath(BASIC_PATH + contentFileModel.getPath());
        return mediaDto;
    }

}
