package com.service;

import com.exception.ServiceException;
import com.persistence.model.ContentFileModel;
import com.persistence.model.RootFolderModel;
import com.persistence.model.UserModel;
import com.persistence.repository.RootFolderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class RootFolderService {
    private final RootFolderRepository rootFolderRepository;

    public RootFolderModel save(RootFolderModel rootFolderModel) {
        rootFolderModel.setFileName(rootFolderModel.getFileName());
        return rootFolderRepository.save(rootFolderModel);
    }

    public List<RootFolderModel> batchSaveRootFolders(List<RootFolderModel> rootFolderModelList) {
        rootFolderModelList.forEach(rootFolderModel -> rootFolderModel.setUuid(UUID.randomUUID().toString()));
        return rootFolderRepository.saveAll(rootFolderModelList);
    }

    public List<RootFolderModel> getAllRootFoldersByUserUuid(UserModel folderCreator) {
        return rootFolderRepository.findAllByFolderCreator(folderCreator).orElseThrow(() -> {
            throw new ServiceException("The folder does not exist");
        });
    }

    public RootFolderModel getRootFolderByUuid(String uuid) {
        return rootFolderRepository.findRootFolderModelByUuid(uuid).orElseThrow(() -> {
            throw new ServiceException("The folder does not exist");
        });
    }

    public Boolean checkFileExistsByName(String parentUuid, String fileName) {
        RootFolderModel rootFolderModel = getRootFolderByUuid(parentUuid);
        Optional<ContentFileModel> fileWithSameName = rootFolderModel.getFiles()
                .stream()
                .filter(subFile -> subFile.getFileName().equals(fileName)).findFirst();
        return fileWithSameName.isPresent();
    }
}
