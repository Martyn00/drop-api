package com.facade;

import com.controller.dto.MediaDto;
import com.exception.MediaException;
import com.persistence.model.ContentFileModel;
import com.service.ContentFileService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class MediaFacade {
    private final ContentFileService contentFileService;
    @Value("${user.files}")
    private String BASIC_PATH;

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
