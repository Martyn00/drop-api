package com.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users_root_folders")
public class RootFolderModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "folderName")
    private String folderName;

    @Column(name = "path")
    private String path;

    @Column(name = "isShared")
    private Boolean shared;

    @OneToOne(fetch = FetchType.LAZY)
    private UserModel folderCreator;

    @OneToMany(fetch = FetchType.LAZY)
    private List<ContentFileModel> files;

}