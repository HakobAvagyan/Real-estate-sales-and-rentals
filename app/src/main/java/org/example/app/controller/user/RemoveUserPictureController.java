package org.example.app.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.exception.ErrorCode;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class RemoveUserPictureController {

    private final UserService userService;

    @GetMapping("/remove/user/picture")
    public String removeUserPicture(@RequestParam("id") int id) {
        if(userService.chekUserById(id)){
            return "redirect:/home?msg=" + ErrorCode.USER_NOT_FOUND.format(id);
        }
        userService.removeUserPicture(userService.findById(id));
        return "redirect:/update?id=" + id;
    }
}