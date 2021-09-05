package com.service;

import com.persistence.model.FileTypeModel;
import com.persistence.repository.FileTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FileTypeService {
    private FileTypeRepository fileTypeRepository;

    public void save(FileTypeModel fileType) {
        fileTypeRepository.save(fileType);
    }
}
