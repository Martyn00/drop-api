package com.persistence.repository;

import com.persistence.model.FileMimeModel;
import com.persistence.model.FileTypeModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileTypeRepository extends JpaRepository<FileTypeModel, Long> {
    Optional<FileTypeModel> findByTypeName(String typeName);

    Optional<FileTypeModel> findFileTypeModelByTypeName(String typeName);

    Optional<FileTypeModel> findFileTypeModelByFileMimesIn(List<FileMimeModel> fileMimes);
}
