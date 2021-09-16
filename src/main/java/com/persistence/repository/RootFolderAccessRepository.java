package com.persistence.repository;

import com.persistence.model.RootFolderAccessModel;
import com.persistence.model.RootFolderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RootFolderAccessRepository extends JpaRepository<RootFolderAccessModel, Long> {
}
