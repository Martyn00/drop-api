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
    private LocalDate addedDate;

    @Column(name = "lastModifiedDate")
    private LocalDate lastModifiedDate;

    @Column(name = "fileSize")
    private Double size;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserModel fileCreator;

    @OneToOne(fetch = FetchType.EAGER)
    private FileTypeModel fileTypeModel;

    @OneToMany(fetch = FetchType.EAGER)
    private List<ContentFileModel> subFiles;

    @ManyToOne(fetch = FetchType.EAGER)
    private RootFolderModel rootFolder;

    @ManyToOne(fetch = FetchType.EAGER)
    private ContentFileModel parentFolder;

}
