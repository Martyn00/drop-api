package com.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoggedUserDto {
    @JsonProperty(value = "firstName")
    protected String firstName;
    @JsonProperty(value = "lastName")
    protected String lastName;
    @JsonProperty(value = "email")
    protected String email;
    @JsonProperty(value = "username")
    protected String username;
    @JsonProperty(value = "uuid")
    private String uuid;
}