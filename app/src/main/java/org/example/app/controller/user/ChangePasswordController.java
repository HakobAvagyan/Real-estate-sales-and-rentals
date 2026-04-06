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
    public String verifyUserPage(@RequestParam("email") String email, ModelMap modelMap) {
        userService.changePasswordByEmail(email);
        modelMap.addAttribute("email", email);
        return "changePassword/changePasswordByVerificationCode";
    }

    @PostMapping("/verify/password")
    public String verifyUserPage(@RequestParam("email") String email,
                                 @RequestParam("verifyCode") String code,
                                 HttpSession session) {
        boolean isVerified = userService.verifyUser(email, code);
        if (isVerified) {
            session.setAttribute("passwordResetVerifiedAt", LocalDateTime.now());
            return "redirect:/change/password?email=" + email;
        }
        return "redirect:/loginPage?msg=" + ErrorCode.VERIFICATION_FAILED.format(email);
    }

    @GetMapping("/change/password")
    public String changePasswordPage(HttpSession session,
                                     RedirectAttributes ra) {
        if (!isRecentlyVerified(session)) {
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
        String email = springUser.getUsername();
        if (!isRecentlyVerified(session)) {
            ra.addFlashAttribute("msg", ErrorCode.VERIFICATION_FAILED.format(email));
            return "redirect:/verify/password";
        }
        if (error.hasErrors() ||
                !request.getNewPassword().equals(request.getConfirmPassword())) {
            ra.addFlashAttribute("msg",
                    ErrorCode.PASSWORD_CHANGE_FAILED.format(email));
            return "redirect:/change/password";
        }
        try {
            userService.changePassword(email,
                    request.getOldPassword(),
                    request.getNewPassword());
        } catch (BusinessException ex) {
            ra.addFlashAttribute("msg", ex.getMessage());
            return "redirect:/change/password";
        }
        session.removeAttribute("passwordResetVerifiedAt");
        return "redirect:/loginPage?msg=" +
                ErrorCode.PASSWORD_CHANGED_SUCCESSFULLY.format(email);
    }



    private boolean isRecentlyVerified(HttpSession session) {
        LocalDateTime verifiedAt = (LocalDateTime) session.getAttribute("passwordResetVerifiedAt");
        if (verifiedAt == null) return false;
        return verifiedAt.isAfter(LocalDateTime.now().minusMinutes(5));
    }

}

