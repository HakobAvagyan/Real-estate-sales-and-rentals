package org.example.app.controller.user;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.user.ChangePasswordRequest;
import org.example.dto.user.ResetPasswordRequest;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.service.UserService;
import org.example.service.security.SpringUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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
        return "redirect:/verify/password/reset?email=" + email;
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
        userService.resetPassword(email, code, request.getNewPassword(), request.getConfirmPassword());
        return "redirect:/loginPage?msg=" + ErrorCode.PASSWORD_CHANGED_SUCCESSFULLY.format(email);
    }

    @GetMapping("/verify/password")
    public String verifyUserPage(
            @RequestParam(value = "email", required = false) String emailParam,
            ModelMap modelMap,
            @AuthenticationPrincipal SpringUser principal) {
        if (principal == null) {
            return "redirect:/loginPage?msg=" + URLEncoder.encode(
                    ErrorCode.USER_NOT_AUTHENTICATED.format(), StandardCharsets.UTF_8);
        }
        String email = principal.getUsername();
        if (emailParam != null && !emailParam.isBlank() && !emailParam.trim().equalsIgnoreCase(email)) {
            return "redirect:/verify/password";
        }
        userService.changePasswordByEmail(email);
        modelMap.addAttribute("email", email);
        return "changePassword/changePasswordByVerificationCode";
    }

    @PostMapping("/verify/password")
    public String verifyUserPage(
            @RequestParam("email") String email,
            @RequestParam("verifyCode") String code,
            HttpSession session,
            @AuthenticationPrincipal SpringUser principal) {
        if (principal == null) {
            return "redirect:/loginPage?msg=" + URLEncoder.encode(
                    ErrorCode.USER_NOT_AUTHENTICATED.format(), StandardCharsets.UTF_8);
        }
        if (!email.trim().equalsIgnoreCase(principal.getUsername())) {
            return "redirect:/loginPage?msg=" + URLEncoder.encode(
                    ErrorCode.TRY_AGAIN.format(), StandardCharsets.UTF_8);
        }
        boolean isVerified = userService.verifyUser(email, code);
        if (isVerified) {
            session.setAttribute("passwordResetVerifiedAt", LocalDateTime.now());
            return "redirect:/change/password";
        }
        return "redirect:/loginPage?msg=" + ErrorCode.VERIFICATION_FAILED.format(email);
    }

    @GetMapping("/change/password")
    public String changePasswordPage(
            HttpSession session,
            RedirectAttributes ra,
            @AuthenticationPrincipal SpringUser principal) {
        if (principal == null) {
            return "redirect:/loginPage?msg=" + URLEncoder.encode(
                    ErrorCode.USER_NOT_AUTHENTICATED.format(), StandardCharsets.UTF_8);
        }
        if (!userService.isRecentlyVerified(session)) {
            ra.addFlashAttribute("msg", "Please verify your email first");
            return "redirect:/verify/password";
        }
        return "changePassword/changePassword";
    }

    @PostMapping("/change/password")
    public String changePassword(@Valid @ModelAttribute ChangePasswordRequest request,
                                 BindingResult error,
                                 @AuthenticationPrincipal SpringUser springUser,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        if (springUser == null) {
            return "redirect:/loginPage?msg=" + URLEncoder.encode(
                    ErrorCode.USER_NOT_AUTHENTICATED.format(), StandardCharsets.UTF_8);
        }
        String email = springUser.getUsername();
        if (!userService.isRecentlyVerified(session)) {
            ra.addFlashAttribute("msg", ErrorCode.VERIFICATION_FAILED.format(email));
            return "redirect:/verify/password";
        }
        if (error.hasErrors()) {
            ra.addFlashAttribute("msg",
                    ErrorCode.PASSWORD_CHANGE_FAILED.format(email));
            return "redirect:/change/password";
        }
        try {
            userService.changePassword(email,
                    request.getOldPassword(),
                    request.getNewPassword(),
                    request.getConfirmPassword());
        } catch (BusinessException ex) {
            ra.addFlashAttribute("msg", ex.getMessage());
            return "redirect:/change/password";
        }
        session.removeAttribute("passwordResetVerifiedAt");
        return "redirect:/loginPage?msg=" +
                ErrorCode.PASSWORD_CHANGED_SUCCESSFULLY.format(email);
    }

}

