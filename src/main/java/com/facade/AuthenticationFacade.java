package com.facade;

import com.persistence.model.UserModel;
import com.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthenticationFacade {
    private final UserService userService;

    public UserModel getUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByUsername(authentication.getName());
    }
}
