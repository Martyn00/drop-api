package com.persistence.repository;

import com.exception.FolderException;
import com.persistence.model.ContentFileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Repository
public interface ContentFileRepository extends JpaRepository<ContentFileModel, Long> {
    Optional<ContentFileModel> getContentFileModelByUuid(String uuid);

    @Override
    @Transactional(rollbackOn = {FolderException.class, AccessDeniedException.class})
    ContentFileModel save(ContentFileModel contentFileModel);

    Optional<ContentFileModel> findContentFileModelByUuid(String uuid);

    @Transactional(rollbackOn = FolderException.class)
    void deleteByUuid(String uuid);

    boolean existsByFileName(String fileName);
}
