package com.controller.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateFolderDto {

    @JsonProperty(value = "folderName")
    private String folderName;

    @JsonProperty(value = "folderId")
    private String folderId;
}