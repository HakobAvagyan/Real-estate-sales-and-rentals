package org.example.app.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.mapper.user.UserRegisterMapper;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class RemoveUserPictureController {

    private final UserService userService;

    @GetMapping("/remove/user/picture")
    public String removeUserPicture(@RequestParam("id") int id) {
        User user = UserRegisterMapper.toUser(userService.findById(id).orElse(null));
        if (user != null) {
            user.setPicName(null);
            userService.save(UserRegisterMapper.toUserRegisterDto(user));
        }
        return "redirect:/update?id=" + id;
    }
}
