package com.service;

import com.persistence.model.RootFolderAccessModel;
import com.persistence.repository.RootFolderAccessRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RootFolderAccessService {

    private RootFolderAccessRepository rootFolderAccessRepository;

    public RootFolderAccessModel save(RootFolderAccessModel rootFolderAccessModel){
        return rootFolderAccessRepository.save(rootFolderAccessModel);
    }

}
