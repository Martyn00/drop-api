package com.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@ToString
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

    @ManyToOne(fetch = FetchType.LAZY)
    private UserModel fileCreator;

    @OneToOne(fetch = FetchType.LAZY)
    private FileTypeModel fileTypeModel;

    @ManyToOne(fetch = FetchType.EAGER)
    private ContentFileModel parentFolder;

    @OneToMany(fetch = FetchType.EAGER)
    private List<ContentFileModel> subFiles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rootFolderModel_id")
    private RootFolderModel rootFolder;

}
