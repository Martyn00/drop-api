package com.persistence.repository;

import com.persistence.model.ContentFileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentFileRepository extends JpaRepository<ContentFileModel, Long> {
}
