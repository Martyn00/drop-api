package com.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@EqualsAndHashCode
@ToString
public class DirectoriesDto {
    @JsonProperty(value = "directories")
    List<DirectoryDto> directories;
}
