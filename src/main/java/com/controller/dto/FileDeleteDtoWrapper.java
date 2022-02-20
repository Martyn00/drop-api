package com.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class FileDeleteDtoWrapper {

    @JsonProperty(value = "filesToDelete")
    private List<FileDeleteDto> fileDeleteDtos;
}
