package com.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FolderSearchDto {

    @JsonProperty(value = "folderId")
    private String folderUuid;

    @JsonProperty(value = "fileName")
    private String fileName;

    @JsonProperty(value = "fileType")
    private String fileType;

    @JsonProperty(value = "searchRange")
    private SearchRangeDto searchRangeDto;
}
