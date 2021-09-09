package com.persistence.repository;

import com.persistence.model.ContentFileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentFileRepository extends JpaRepository<ContentFileModel, Long> {
    Optional<ContentFileModel> getContentFileModelByUuid(String uuid);

    Optional<ContentFileModel> findContentFileModelByUuid(String uuid);
}
