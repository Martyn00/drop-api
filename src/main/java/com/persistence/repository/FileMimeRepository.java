package com.persistence.repository;

import com.persistence.model.FileMimeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileMimeRepository extends JpaRepository<FileMimeModel, Long> {
    Optional<FileMimeModel> findFileMimeModelByFileDetail(String fileDetail);
}
