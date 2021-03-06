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
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserFacade userFacade;

    @PostMapping(path = "/register", produces = "application/json", consumes = "application/json")
    public ResponseEntity<DisplayUserDto> registerUser(@RequestBody @Valid UserDto userDto) {
        DisplayUserDto displayUserDto = userFacade.createUser(userDto);
        return new ResponseEntity<>(displayUserDto, HttpStatus.CREATED);
    }

    @GetMapping(path = "/email-exists/{email}")
    public ResponseEntity<Boolean> checkUserExistsByEmail(@PathVariable String email) {
        return new ResponseEntity<>(userFacade.checkUserExistsByEmail(email), HttpStatus.OK);
    }

    @GetMapping(path = "/username-exists/{username}")
    public ResponseEntity<Boolean> checkUserExistsByUsername(@PathVariable String username) {
        return new ResponseEntity<>(userFacade.checkUserExistsByUsername(username), HttpStatus.OK);
    }

}
