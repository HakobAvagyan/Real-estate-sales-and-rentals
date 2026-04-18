package org.example.app.controller.user;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.user.UserRegisterDto;
import org.example.dto.user.UserResponseDto;
import org.example.dto.user.UserUpdateDto;
import org.example.exception.ErrorCode;
import org.example.model.enums.Role;
import org.example.service.PropertyService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final PropertyService propertyService;

    @GetMapping("/admin/home")
    public String adminHomePage(ModelMap modelMap) {
        List<UserResponseDto> userList = userService.findAll();
        modelMap.addAttribute("users", userList);
        return "admin/adminHome";
    }

    @GetMapping("/user/home")
    public String userHomePage(@AuthenticationPrincipal SpringUser principal, ModelMap modelMap) {
        if (principal == null) {
            log.error("Unauthorized access attempt to user home page user: {}", principal);
            return "redirect:/home";
        }
        modelMap.addAttribute("properties", propertyService.findAllByUserId(principal.getUser().getId()));
        return "user/userHome";
    }

    @GetMapping("/manager/home")
    public String managerHomePage(ModelMap modelMap) {
        List<UserResponseDto> userList = userService.
                findUserByRole(Role.USER);
        modelMap.addAttribute("users", userList);
        return "manager/managerHome";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam("id") int id) {
        userService.deleteById(id);
        return "redirect:/logout";
    }

    @GetMapping("/blocked")
    public String blockedUser(@RequestParam("id") int id) {
        userService.toggleUserBlockStatus(id);
        return "redirect:/home";
    }

    @GetMapping("/update")
    public String editUser(
            @RequestParam("id") int id,
            ModelMap modelMap,
            @AuthenticationPrincipal SpringUser principal) {
        if (principal == null) {
            log.error("Unauthorized access attempt to user update page user: {} in GetMapping", principal);
            return "redirect:/home";
        }
        if (principal.getUser().getId() != id && principal.getUser().getRole() != Role.ADMIN) {
            return "redirect:/personalPage?id=" + id;
        }
        UserResponseDto user = userService.findById(id);
        modelMap.addAttribute("user", user);
        return "update";
    }

    @PostMapping("/update")
    public String editUser(
            @RequestParam(value = "id") int id,
            @ModelAttribute UserUpdateDto user,
            @RequestParam(value = "pic") MultipartFile multipartFile,
            @AuthenticationPrincipal SpringUser principal) {
        if (principal == null) {
            log.error("Unauthorized access attempt to user update page user: {} in PostMapping", principal);
            return "redirect:/home";
        }
        if (principal.getUser().getId() != id && principal.getUser().getRole() != Role.ADMIN) {
            log.error("Unauthorized access attempt to user update page user: {}, id: {}", principal,id);
            return "redirect:/personalPage?id=" + id;
        }
        userService.update(user, multipartFile,id);
        return "redirect:/personalPage?id=" + id;
    }

    @GetMapping("/loginPage")
    public String loginPage(@RequestParam(required = false) String msg,
                            ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "loginPage";
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(required = false) String msg,
                               ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "registerPage";
    }


    @PostMapping("/register")
    public String register(@Valid @ModelAttribute UserRegisterDto registeredUser,
                           @RequestParam(value = "pic") MultipartFile multipartFile,
                           BindingResult errors,
                           HttpSession session) {

        String email = registeredUser.getEmail();
        if (errors.hasErrors()) {
            log.error("Invalid email address: {}", email);
            return "redirect:/register?msg=" + ErrorCode.TRY_AGAIN.format(email);
        }
        if (userService.findByEmail(email) != null) {
            log.error("User with email {} already exists", email);
            return "redirect:/register?msg=" +
                    ErrorCode.USER_ALREADY_REGISTERED.format(email);
        }
        registeredUser.setBlocked(true);
        userService.save(registeredUser, multipartFile);
        session.setAttribute("verifyEmail", email);
        return "redirect:/verify";
    }

    @GetMapping("/admin/add/manager")
    public String addManagerPage(@RequestParam(required = false) String msg,
                                 ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "admin/addManager";
    }

    @PostMapping("/admin/add/manager")
    public String addManager(@Valid @ModelAttribute UserRegisterDto manager,
                             @RequestParam(value = "pic") MultipartFile multipartFile,
                             HttpSession session) {
        String email = manager.getEmail();
        if (userService.findByEmail(email) != null) {
            log.error("Manager with email {} already exists", email);
            return "redirect:/admin/add/manager?msg="  +
                    ErrorCode.USER_ALREADY_REGISTERED.format(email);
        }
        userService.createManager(manager, multipartFile);
        session.setAttribute("verifyEmail", email);
        return "redirect:/verify";
    }


    @GetMapping("/verify")
    public String verifyUserPage(HttpSession session, ModelMap modelMap) {
        String email = (String) session.getAttribute("verifyEmail");
        if (email == null) {
            log.error("Invalid email address for verify : {} in GetMapping", email);
            return "redirect:/register";
        }
        modelMap.addAttribute("email", email);
        return "verifyUser";
    }

    @PostMapping("/verify")
    public String verifyUser(HttpSession session,
                             @RequestParam("verifyCode") String verifyCode) {
        String email = (String) session.getAttribute("verifyEmail");
        if (email == null) {
            log.error("Invalid email address for verify : {} in PostMapping", email);
            return "redirect:/register";
        }
        boolean isVerified = userService.verifyUser(email, verifyCode);
        if (isVerified) {
            session.removeAttribute("verifyEmail");
            return "redirect:/loginPage?msg=" + ErrorCode.VERIFICATION_SUCCESSFUL.format(email);
        }

        return "redirect:/verify?msg=" + ErrorCode.VERIFICATION_FAILED.format(email);
    }


}
