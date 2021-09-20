package com.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class DirectoriesDto {
    @JsonProperty(value = "directories")
    List<DirectoryDto> directories;
}
