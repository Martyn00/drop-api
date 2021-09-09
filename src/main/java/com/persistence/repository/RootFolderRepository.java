package com.persistence.repository;

import com.persistence.model.RootFolderModel;
import com.persistence.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RootFolderRepository extends JpaRepository<RootFolderModel, Long> {

    Optional<List<RootFolderModel>> findAllByFolderCreator(UserModel folderCreator);

    Optional<RootFolderModel> findRootFolderModelByUuid(String Uuid);

}