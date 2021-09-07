package com.controller;

import com.controller.dto.DisplayUserDto;
import com.controller.dto.UserDto;
import com.facade.UserFacade;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/users")
@AllArgsConstructor
public class UserController {

    private final UserFacade userFacade;

    @PostMapping(path = "/register", produces = "application/json", consumes = "application/json")
    public ResponseEntity<DisplayUserDto> registerUser(@RequestBody @Valid UserDto userDto) {
        DisplayUserDto displayUserDto = userFacade.createUser(userDto);
        return new ResponseEntity<>(displayUserDto, HttpStatus.CREATED);
    }

    @GetMapping(path = "/test", produces = "applictaion/json")
    public ResponseEntity<String> test(){
        return new ResponseEntity<>("TEST", HttpStatus.OK);
    }
}
