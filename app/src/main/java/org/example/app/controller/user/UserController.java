package org.example.app.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserRegisterDto;
import org.example.dto.user.UserRequestDto;
import org.example.exception.ErrorCode;
import org.example.model.enums.Role;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/admin/home")
    public String adminHomePage(ModelMap modelMap) {
        List<UserRequestDto> userList = userService.findAll();
        modelMap.addAttribute("users", userList);
        return "admin/adminHome";
    }

    @GetMapping("/user/home")
    public String userHomePage() {
        return "user/userHome";
    }

    @GetMapping("/manager/home")
    public String managerHomePage(ModelMap modelMap) {
        List<UserRequestDto> userList = userService.findAllByRoleIn(List.of(Role.USER, Role.CUSTOMER));
        modelMap.addAttribute("users", userList);
        return "manager/managerHome";
    }

    @GetMapping("/customer/home")
    public String customerHomePage() {
        return "customer/customerHome";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam("id") int id) {
        userService.deleteById(id);
        return "redirect:/loginPage";
    }

    @GetMapping("/blocked")
    public String blockedUser(@RequestParam("id") int id) {
        userService.findById(id).ifPresent(user -> {
            user.setBlocked(!user.isBlocked());
            userService.save(user);
        });
        return "redirect:/home";
    }

    @GetMapping("/update")
    public String editUser(@RequestParam("id") int id, ModelMap modelMap) {

        userService.findById(id).ifPresent(
                user -> {
                    modelMap.addAttribute("user", user);
                }
        );
        return "update";
    }

    @PostMapping("/update")
    public String editUser(@ModelAttribute UserRegisterDto user,
                           @RequestParam(value = "pic") MultipartFile multipartFile) {
        userService.update(user,multipartFile);
        return "redirect:/personalPage?id=" + user.getId();

    }

    @GetMapping("/loginPage")
    public String loginPage(@RequestParam(required = false) String msg, ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "loginPage";
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(required = false) String msg, ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "registerPage";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserRegisterDto registeredUser,
                           @RequestParam(value = "pic") MultipartFile multipartFile) {
        if (userService.findByEmail(registeredUser.getEmail()).isPresent()) {
            return "redirect:/register?msg=" + ErrorCode.USER_ALREADY_REGISTERED.format(registeredUser.getEmail());
        }
        registeredUser.setBlocked(true);
        userService.save(registeredUser, multipartFile);
        return "redirect:/verify?email=" + registeredUser.getEmail();
    }

    @GetMapping("/admin/add/manager")
    public String addManagerPage(@RequestParam(required = false) String msg, ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "admin/addManager";
    }

    @PostMapping("/admin/add/manager")
    public String addManager(@ModelAttribute UserRegisterDto manager,
                             @RequestParam(value = "pic") MultipartFile multipartFile) {
        if (userService.findByEmail(manager.getEmail()).isPresent()) {
            return "redirect:/admin/add/manager?msg="  + ErrorCode.USER_ALREADY_REGISTERED.format(manager.getEmail());
        }
        manager.setRole(Role.MANAGER);
        manager.setBlocked(true);
        userService.save(manager, multipartFile);
        return "redirect:/verify?email=" + manager.getEmail();
    }

    @GetMapping("/verify")
    public String verifyUserPage(@RequestParam("email") String email, ModelMap modelMap) {
        modelMap.addAttribute("email", email);
        return "verifyUser";
    }

    @PostMapping("/verify")
    public String verifyUser(@RequestParam("email") String email, @RequestParam("verifyCode") String verifyCode) {
        boolean isVerified = userService.verifyUser(email, verifyCode);
        if (isVerified) {
            userService.findByEmail(email).ifPresent(user -> {;
                userService.save(user);
            });
            return "redirect:/loginPage?msg=User verified successfully, pls Login!";
        }
        return "redirect:/loginPage?msg=Verification code is invalid!";
    }

}
