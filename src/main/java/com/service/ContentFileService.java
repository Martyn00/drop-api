package com.service;

import com.exception.ServiceException;
import com.persistence.model.ContentFileModel;
import com.persistence.repository.ContentFileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ContentFileService {
    ContentFileRepository contentFileRepository;

    public ContentFileModel save(ContentFileModel fileModel) {
        return contentFileRepository.save(fileModel);
    }

    public ContentFileModel findContentFileModelByUuid(String uuid) {
        return contentFileRepository.findContentFileModelByUuid(uuid).orElseThrow(() -> {
            throw new ServiceException("The file or folder does not exist");
        });
    }
}
