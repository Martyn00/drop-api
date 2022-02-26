package com.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Setter
@Getter
public class DirectoryDto {
    @JsonProperty(value = "id")
    private String uuid;

    @JsonProperty(value = "fileName")
    private String fileName;

    @JsonProperty(value = "parentId")
    private String parentUuid;

    @JsonProperty(value = "path")
    private String path;

    @JsonProperty(value = "fullPath")
    private String fullPath;

    @JsonProperty(value = "subfolders")
    private List<DirectoryDto> subfolders;
}
