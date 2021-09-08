package com.service;

import com.exception.ServiceException;
import com.persistence.model.FileTypeModel;
import com.persistence.repository.FileTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FileTypeService {
    private FileTypeRepository fileTypeRepository;

    private Boolean checkIfTypeExists(String type) {
        return fileTypeRepository.findByTypeName(type).isPresent();
    }

    public FileTypeModel save(FileTypeModel fileType) {
        if (checkIfTypeExists(fileType.getTypeName())) {
            throw new ServiceException("FileType is already addded in the database");
        }
        return fileTypeRepository.save(fileType);
    }

}
