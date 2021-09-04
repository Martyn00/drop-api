package com.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LoggedResponseDto {

    @JsonProperty(value = "token")
    private String token;

    @JsonProperty(value = "loggedUser")
    private LoggedUserDto loggedUserDto;
}