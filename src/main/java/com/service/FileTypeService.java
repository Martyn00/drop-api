package com.service;

import com.exception.ServiceException;
import com.persistence.model.FileMimeModel;
import com.persistence.model.FileTypeModel;
import com.persistence.repository.FileMimeRepository;
import com.persistence.repository.FileTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FileTypeService {
    private FileTypeRepository fileTypeRepository;
    private FileMimeRepository FileMimeRepository;

    private Boolean checkIfTypeExists(String type) {
        return fileTypeRepository.findByTypeName(type).isPresent();
    }

    public FileTypeModel save(FileTypeModel fileType) {
        if (checkIfTypeExists(fileType.getTypeName())) {
            throw new ServiceException("FileType is already addded in the database");
        }
        return fileTypeRepository.save(fileType);
    }

    public List<FileTypeModel> getAllFileTypes() {
        return fileTypeRepository.findAll();
    }

    public FileTypeModel getFileTypeByName(String fileType) {
        return fileTypeRepository.findFileTypeModelByTypeName(fileType).orElseThrow(() ->
        {
            throw new ServiceException("File type not found");
        });
    }

    public FileTypeModel findFileTypeByFileMime(String fileMime) {
        Optional<FileMimeModel> fileMimeModelOptional = FileMimeRepository.findFileMimeModelByFileDetail(fileMime);
        List<FileMimeModel> fileMimeModels = new ArrayList<>();
        fileMimeModelOptional.ifPresent(fileMimeModels::add);
        return findFileTypeByFileMimes(fileMimeModels);
    }

    private FileTypeModel findFileTypeByFileMimes(List<FileMimeModel> fileMimeModels) {
        if (fileMimeModels.isEmpty()) {
            return fileTypeRepository.findFileTypeModelByTypeName("unknown").get();
        }
        return fileTypeRepository.findFileTypeModelByFileMimesIn(fileMimeModels).get();
    }
}
