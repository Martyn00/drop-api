package com.persistence.repository;

import com.persistence.model.FileTypeModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileTypeRepository extends JpaRepository<FileTypeModel, Long> {
    Optional<FileTypeModel> findByTypeName(String typeName);
}
