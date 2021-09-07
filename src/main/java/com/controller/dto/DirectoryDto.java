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

    @JsonProperty(value = "parentUuid")
    private String parentUuid;

    @JsonProperty(value = "subfolders")
    private List<DirectoryDto> subfolders;
}
