package com.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RenameFolderDto {

    @JsonProperty(value = "newName")
    private String folderName;

    @JsonProperty(value = "folderId")
    private String folderId;
}
