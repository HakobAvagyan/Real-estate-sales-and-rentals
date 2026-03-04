package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;


    @GetMapping("/home")
    public String homePage(ModelMap modelMap) {
        List<User> userList = userService.findAll();
        modelMap.addAttribute("users", userList);
        return "home";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam("id") int id) {
        userService.deleteById(id);
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
    public String editUser(@ModelAttribute User user,
                           @RequestParam(value = "pic") MultipartFile multipartFile,
                           @RequestParam(value = "remove" , required = false) String remove
    ) {
        if("true".equals(remove)) {
            user.setPicName(null);
            userService.update(user);
        }else  {
            userService.save(user,multipartFile);
        }
        return "redirect:/home";

    }

    @GetMapping("/loginPage")
    public String loginPage(@RequestParam(required = false) String msg, ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "index";
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(required = false) String msg, ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "registerPage";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User registeredUser,
                           @RequestParam(value = "pic") MultipartFile multipartFile) {
        if (userService.findByEmail(registeredUser.getEmail()).isPresent()) {
            return "redirect:/register?msg=Username already exists!";
        }
        registeredUser.setPassword(passwordEncoder.encode(registeredUser.getPassword()));
        userService.save(registeredUser, multipartFile);
        return "redirect:/loginPage";
    }



}
