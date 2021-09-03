package com.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users_folders_folders")
public class ContentFileModel {
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

    @OneToOne(fetch = FetchType.LAZY)
    private RootFolderModel rootFolder;

    @Column(name = "addedDate")
    private LocalDate addedDate;

    @Column(name = "lastModifiedDate")
    private LocalDate lastModifiedDate;

    @Column(name = "fileSize")
    private Double size;

    @OneToOne(fetch = FetchType.LAZY)
    private UserModel fileCreator;

    @OneToMany(fetch = FetchType.LAZY)
    private List<ContentFileModel> files;

}
