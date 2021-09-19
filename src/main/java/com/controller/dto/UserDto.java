package com.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UserDto {
    @NotEmpty(message = "firstName cannot be empty")
    @JsonProperty(value = "firstName")
    private String firstName;

    @NotEmpty(message = "lastName cannot be empty")
    @JsonProperty(value = "lastName")
    private String lastName;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email is not valid", regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
    @JsonProperty(value = "email")
    private String email;

    @NotEmpty(message = "Username cannot be empty")
    @JsonProperty(value = "username")
    private String username;

    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 10, max = 20, message = "Password should be between 10 and 20 characters")
    @JsonProperty(value = "password")
    private String password;
}
