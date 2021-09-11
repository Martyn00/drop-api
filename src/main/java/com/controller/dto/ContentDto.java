package com.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentDto {

    @JsonProperty(value = "parentDirectory")
    FileMetadataDto contentFileDto;

    @JsonProperty(value = "files")
    List<FileMetadataDto> contentFileDtos;
}
