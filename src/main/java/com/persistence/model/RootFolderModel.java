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
@Table(name = "root_folders")
public class RootFolderModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "folderName")
    private String fileName;

    @Column(name = "path")
    private String path;

    @Column(name = "isShared")
    private Boolean shared;

    @OneToOne(fetch = FetchType.EAGER)
    private UserModel folderCreator;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    private List<ContentFileModel> files;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<RootFolderAccessModel> rootFolderAccessModel;
}