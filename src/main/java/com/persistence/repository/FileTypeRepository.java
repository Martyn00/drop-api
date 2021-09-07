package com.persistence.repository;

import com.persistence.model.FileTypeModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileTypeRepository extends JpaRepository<FileTypeModel, Long> {
}
