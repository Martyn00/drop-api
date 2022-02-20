package com.controller.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FileDeleteDto {

    @JsonProperty(value = "fileToDeleteUuid")
    private String fileToDeleteUuid;

}
