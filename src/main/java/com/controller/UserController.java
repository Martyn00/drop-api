package com.controller;
import com.controller.dto.UserDto;
import com.facade.UserFacade;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/users")
@AllArgsConstructor
public class UserController {
    UserFacade userFacade;

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserDto userDto) {
        userFacade.createUser(userDto);
        return new ResponseEntity(userDto.toString(), HttpStatus.CREATED);
    }
}