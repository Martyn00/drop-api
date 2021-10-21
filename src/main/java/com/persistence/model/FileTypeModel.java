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
@Table(name = "file_types")
public class FileTypeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "typeName", unique = true)
    private String typeName;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany
    @JoinTable(name = "file_types_file_mimes",
            joinColumns = @JoinColumn(name = "file_type_model_id"), inverseJoinColumns = @JoinColumn(name = "file_mime_id"))
    private List<FileMimeModel> fileMimes;

}
