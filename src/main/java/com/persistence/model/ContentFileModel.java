package com.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "folders_content")
public class ContentFileModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "fileName")
    private String fileName;

    @Column(name = "path")
    private String path;

    @Column(name = "addedDate")
    private ZonedDateTime addedDate;

    @Column(name = "lastModifiedDate")
    private ZonedDateTime lastModifiedDate;

    @Column(name = "fileSize")
    private Double size;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserModel fileCreator;

    @OneToOne(fetch = FetchType.EAGER)
    private FileTypeModel fileTypeModel;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ContentFileModel> subFiles;

    @ManyToOne(fetch = FetchType.EAGER)
    private RootFolderModel rootFolder;

    @ManyToOne(fetch = FetchType.EAGER)
    private ContentFileModel parentFolder;

    @Column(name = "fileMime")
    private String fileMime;
}
