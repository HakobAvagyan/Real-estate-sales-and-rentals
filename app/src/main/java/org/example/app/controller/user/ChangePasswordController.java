package org.example.app.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserRegisterDto;
import org.example.exception.ErrorCode;
import org.example.model.User;
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

    @GetMapping("/change/password/byEmail")
    public String changePassword(@RequestParam(required = false) String msg, ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "changePassword/changePasswordByEmailPage";
    }

    @PostMapping("/change/password/byEmail")
    public String changePassword(@RequestParam("email")  String email) {
        Optional<UserRegisterDto> byEmail = userService.findByEmail(email);
        if(byEmail.isEmpty()) {
            return "redirect:/change/password?msg=" + ErrorCode.USER_NOT_FOUND_BY_EMAIL.format(email);
        }
        return "redirect:/verify/password?email=" + email;
    }

    @GetMapping("/verify/password")
    public String verifyUserPage(@RequestParam("email") String email, ModelMap modelMap) {
        userService.changePasswordByEmail(email);
        modelMap.addAttribute("email", email);
        return "changePassword/chnagePasswordByVerifycationCode";
    }

    @PostMapping("/verify/password")
    public String verifyUserPage(@RequestParam("email") String email, @RequestParam("verifyCode") String code) {
        boolean isVerified = userService.verifyUser(email, code);
        if (isVerified) {
            userService.findByEmail(email).ifPresent(user -> {;
                userService.save(user);
            });
            return "redirect:/change/password?email="  + email;
        }
        return "redirect:/loginPage?msg=Verification failed, pls try again!";
    }
    @GetMapping("/change/password")
    public String changePasswordPage(@RequestParam("email") String email,@RequestParam(required = false) String msg, ModelMap modelMap) {
        modelMap.addAttribute("email", email);
        modelMap.addAttribute("msg", msg);
        return "changePassword/changePassword";
    }

    @PostMapping("/change/password")
    public String changePassword(@RequestParam("email") String email,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam(value = "oldPassword",required = false) String oldPassword,
                                 @RequestParam(value = "confirmPassword",required = false) String confirmPassword) {

        Optional<UserRegisterDto> optionalUser = userService.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "redirect:/loginPage?msg=" + ErrorCode.USER_NOT_FOUND_BY_EMAIL.format(email);
        }
        UserRegisterDto user = optionalUser.get();

        if (oldPassword != null && confirmPassword != null) {
            if (!userService.checkOldPassword(oldPassword, email)) {
                return "redirect:/change/password?msg=Old password is incorrect";
            }
            if (!newPassword.equals(confirmPassword)) {
                return "redirect:/change/password?msg=Passwords do not match";
            }
            userService.changePassword(email, newPassword);
            return "redirect:/personalPage?id=" + user.getId();
        }
        userService.changePassword(email, newPassword);
        return "redirect:/loginPage?msg=" + ErrorCode.PASSWORD_CHANGED_SUCCESSFULLY.format(email);
    }




}
