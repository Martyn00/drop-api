package com.service;

import com.exception.ServiceException;
import com.persistence.model.RootFolderModel;
import com.persistence.model.UserModel;
import com.persistence.repository.RootFolderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class RootFolderService {
    private final RootFolderRepository rootFolderRepository;

    public RootFolderModel saveRootFolder(RootFolderModel rootFolderModel) {
        rootFolderModel.setFolderName(UUID.randomUUID().toString());
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
}
