package com.controller.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FilesDeleteDto {

    @JsonProperty(value = "filesToDeleteIds")
    private List<String> filesToDeleteUuids;

}
