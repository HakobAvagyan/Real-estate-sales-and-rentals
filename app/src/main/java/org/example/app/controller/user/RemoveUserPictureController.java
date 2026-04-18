package org.example.app.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.BusinessException;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RemoveUserPictureController {

    private final UserService userService;

    @GetMapping("/remove/user/picture")
    public String removeUserPicture(@RequestParam("id") int id) {
        try {
            userService.removeUserPicture(id);
        } catch (BusinessException ex) {
            log.error("Failed to remove user picture for user id: {}. Error: {}", id, ex.getMessage());
            return "redirect:/personalPage?id=" + id;
        }
        return "redirect:/update?id=" + id;
    }
}