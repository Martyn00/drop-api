package com.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class PossibleUserDto {
    @JsonProperty(value = "username")
    private String username;
    @JsonProperty(value = "id")
    private String uuid;
}
