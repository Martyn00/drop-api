package com.service;

import com.exception.ServiceException;
import com.persistence.model.ContentFileModel;
import com.persistence.repository.ContentFileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;


@AllArgsConstructor
@Service
public class ContentFileService {

    private ContentFileRepository contentFileRepository;

    public ContentFileModel save(ContentFileModel fileModel) {
        return contentFileRepository.save(fileModel);
    }

    public ContentFileModel findContentFileModelByUuid(String uuid) {
        return contentFileRepository.findContentFileModelByUuid(uuid).orElseThrow(() -> {
            throw new ServiceException("The file or folder does not exist");
        });
    }

    public ContentFileModel getFileByUuid(String uuid) {
        return contentFileRepository.getContentFileModelByUuid(uuid).
                orElseThrow(() -> {
                    throw new ServiceException(MessageFormat.format("File with uuid {0} not found", uuid));
                });
    }

    public void deleteFileByUuid(String uuid) {
        contentFileRepository.deleteByUuid(uuid);
    }

    public Boolean checkFileExistsByName(String parentUuid, String fileName) {
        ContentFileModel parentFolder = findContentFileModelByUuid(parentUuid);
        Optional<ContentFileModel> fileWithSameName = parentFolder.getSubFiles()
                .stream()
                .filter(subFile -> subFile.getFileName().equals(fileName)).findFirst();
        return fileWithSameName.isPresent();
    }

    public List<ContentFileModel> batchSave(List<ContentFileModel> fileModels) {
        return contentFileRepository.saveAll(fileModels);
    }
}
