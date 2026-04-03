package org.example.app.controller.user;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserRegisterDto;
import org.example.dto.user.UserRequestDto;
import org.example.exception.ErrorCode;
import org.example.model.enums.Role;
import org.example.service.UserService;
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
        List<UserRequestDto> userList = userService.
                findAllByRoleIn(List.of(Role.USER, Role.CUSTOMER));
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
        return "redirect:/logout";
    }

    @GetMapping("/blocked")
    public String blockedUser(@RequestParam("id") int id) {
        userService.toggleUserBlockStatus(id);
        return "redirect:/home";
    }

    @GetMapping("/update")
    public String editUser(@RequestParam("id") int id, ModelMap modelMap) {
        UserRequestDto user = userService.findById(id);
        if (user == null) {
            return "redirect:/home?msg=" +ErrorCode.USER_NOT_FOUND.format(id);
        }
        modelMap.addAttribute("user", user);
        return "update";
    }

    @PostMapping("/update")
    public String editUser(@ModelAttribute UserRegisterDto user,
                           @RequestParam(value = "pic") MultipartFile multipartFile) {
        userService.update(user,multipartFile);
        return "redirect:/personalPage?id=" + user.getId();

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
            return "redirect:/register?msg=" + ErrorCode.TRY_AGAIN.format(email);
        }
        if (userService.findByEmail(email) != null) {
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
                             @RequestParam(value = "pic") MultipartFile multipartFile) {
        if (userService.findByEmail(manager.getEmail()) != null) {
            return "redirect:/admin/add/manager?msg="  +
                    ErrorCode.USER_ALREADY_REGISTERED.format(manager.getEmail());
        }
        userService.createManager(manager, multipartFile);
        return "redirect:/verify?email=" + manager.getEmail();
    }


    @GetMapping("/verify")
    public String verifyUserPage(HttpSession session, ModelMap modelMap) {
        String email = (String) session.getAttribute("verifyEmail");
        if (email == null) {
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
            return "redirect:/register";
        }
        boolean isVerified = userService.verifyUser(email, verifyCode);
        if (isVerified) {
            session.removeAttribute("verifyEmail"); // 🔥 clean
            return "redirect:/loginPage?msg=" + ErrorCode.VERIFICATION_SUCCESSFUL.format(email);
        }

        return "redirect:/verify?msg=" + ErrorCode.VERIFICATION_FAILED.format(email);
    }


}
