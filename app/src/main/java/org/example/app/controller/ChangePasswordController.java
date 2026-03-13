package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserRegisterDto;
import org.example.exception.ErrorCode;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ChangePasswordController {
    private final UserService userService;

    @GetMapping("/change/password")
    public String changePassword(@RequestParam(required = false) String msg, ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "changePasswordPage";
    }

    @PostMapping("/change/password")
    public String changePassword(@RequestParam("email")  String email,@RequestParam("newPassword") String newPassword) {
        if(userService.findByEmail(email).isEmpty()) {
            return "redirect:/change/password?msg=" + ErrorCode.USER_NOT_FOUND_BY_EMAIL.format(email);
        }
        Optional<UserRegisterDto> userRegisterDto = userService.changePassword(email, newPassword);
        if(userRegisterDto.isPresent()) {
            return "redirect:/loginPage?msg=" + ErrorCode.PASSWORD_CHANGED_SUCCESSFULLY.format(email);
        }
        return "redirect:/change/password?msg=" + ErrorCode.PASSWORD_CHANGE_FAILED.format(email);
    }
}
