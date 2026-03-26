package org.example.app.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.user.ChangePasswordRequest;
import org.example.dto.user.ResetPasswordRequest;
import org.example.exception.ErrorCode;
import org.example.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ChangePasswordController {

    private final UserService userService;

    @GetMapping("/reset/password/byEmail")
    public String resetPasswordByEmail(@RequestParam(required = false) String msg, ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "changePassword/changePasswordByEmailPage";
    }

    @PostMapping("/reset/password/byEmail")
    public String resetPasswordByEmail(@RequestParam("email") String email) {
        userService.changePasswordByEmail(email);
        return"redirect:/verify/password/reset?email="+email;
    }

    @GetMapping("/verify/password/reset")
    public String resetPassword(@RequestParam("email") String email, ModelMap modelMap) {
        modelMap.addAttribute("email", email);
        return "changePassword/resetPassword";
    }

    @PostMapping("/verify/password/reset")
    public String resetPassword(@Valid @ModelAttribute ResetPasswordRequest request,
                                @RequestParam("email") String email,
                                @RequestParam("verifyCode") String code,
                                BindingResult error
                                ) {
            if (error.hasErrors()) {
                return "redirect:/loginPage?msg=" + ErrorCode.PASSWORD_CHANGE_FAILED.format(email);
            }
            userService.resetPassword(email,code, request.getNewPassword(), request.getConfirmPassword());
            return "redirect:/loginPage?msg=" + ErrorCode.PASSWORD_CHANGED_SUCCESSFULLY.format(email);
    }

    @GetMapping("/verify/password")
    public String verifyUserPage(@RequestParam("email") String email, ModelMap modelMap) {
        userService.changePasswordByEmail(email);
        modelMap.addAttribute("email", email);
        return "changePassword/changePasswordByVerificationCode ";
    }

    @PostMapping("/verify/password")
    public String verifyUserPage(@RequestParam("email") String email, @RequestParam("verifyCode") String code) {
        boolean isVerified = userService.verifyUser(email, code);
        if (isVerified) {
            return "redirect:/change/password?email=" + email;
        }
        return "redirect:/loginPage?msg=Verification failed, pls try again!";
    }


    @GetMapping("/change/password")
    public String changePasswordPage(@RequestParam("email") String email, @RequestParam(required = false) String msg, ModelMap modelMap) {
        modelMap.addAttribute("email", email);
        modelMap.addAttribute("msg", msg);
        return "changePassword/changePassword";
    }

    @PostMapping("/change/password")
    public String changePassword(@Valid @ModelAttribute ChangePasswordRequest request, BindingResult error) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authEmail = auth.getName();
        if (error.hasErrors()) {
            return "redirect:/change/password?msg=" + ErrorCode.PASSWORD_CHANGE_FAILED.format(authEmail);
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return "redirect:/change/password?msg=" + ErrorCode.PASSWORD_CHANGE_FAILED.format(authEmail);
        }
        try {
            userService.changePassword(authEmail, request.getOldPassword(), request.getNewPassword());
        } catch (IllegalArgumentException ex) {
            return "redirect:/change/password?msg=" + ex.getMessage();
        }
        return "redirect:/loginPage?msg=" + ErrorCode.PASSWORD_CHANGED_SUCCESSFULLY.format(authEmail);
    }

}
