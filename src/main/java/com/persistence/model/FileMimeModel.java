package com.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "file_mimes")
public class FileMimeModel {
    @Column(name = "file_mime")
    String fileDetail;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinTable(name = "file_types_file_mimes",
            inverseJoinColumns = @JoinColumn(name = "file_type_model_id"), joinColumns = @JoinColumn(name = "file_mime_id"))
    private FileTypeModel fileType;
}
