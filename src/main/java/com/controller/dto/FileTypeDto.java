package com.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileTypeDto {
    @JsonProperty(value = "id")
    private String uuid;

    @JsonProperty(value = "type")
    private String typeName;

    @JsonProperty(value = "is_active")
    private Boolean isActive;

}
