package org.example.app.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.example.model.User;
import org.example.service.security.SpringUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@Slf4j
public class UserControllerAdvice {

    @ModelAttribute("currentUser")
    public User getUser(@AuthenticationPrincipal SpringUser springUser){

        if (springUser == null){
            return null;
        }
        return springUser.getUser();
    }
}
