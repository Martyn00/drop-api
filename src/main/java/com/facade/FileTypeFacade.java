package com.facade;

import com.controller.dto.FileTypeDto;
import com.persistence.model.FileTypeModel;
import com.service.FileTypeService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class FileTypeFacade {
    private FileTypeService fileTypeService;
    private ModelMapper modelMapper;

    public FileTypeDto addFileType(String type) {
        FileTypeModel fileTypeModel = createFileTypeModel(type);
        return modelMapper.map(fileTypeService.save(fileTypeModel), FileTypeDto.class);
    }

    public List<FileTypeDto> getAllFileTypes() {
        return fileTypeService.getAllFileTypes()
                .stream()
                .map(fileTypeModel -> modelMapper.map(fileTypeModel, FileTypeDto.class))
                .collect(Collectors.toList());
    }

    private FileTypeModel createFileTypeModel(String type) {
        FileTypeModel fileTypeModel = new FileTypeModel();
        fileTypeModel.setIsActive(false);
        fileTypeModel.setTypeName(type);
        fileTypeModel.setUuid(UUID.randomUUID().toString());
        return fileTypeModel;
    }
}
