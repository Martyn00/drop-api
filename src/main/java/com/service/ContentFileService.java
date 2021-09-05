package com.service;

import com.persistence.model.ContentFileModel;
import com.persistence.repository.ContentFileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ContentFileService {
    ContentFileRepository contentFileRepository;

    public void save(ContentFileModel fileModel) {
        contentFileRepository.save(fileModel);
    }
}
