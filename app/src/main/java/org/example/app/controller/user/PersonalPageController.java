package org.example.app.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.model.enums.Role;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PersonalPageController {

    private final UserService userService;

    @GetMapping("/personalPage")
    public String personalPage(@RequestParam("id") int id, ModelMap modelMap) {
        userService.findById(id).ifPresent(
                user -> {
                    modelMap.addAttribute("user", user);
                }
        );
        modelMap.addAttribute("role", Role.ADMIN);
        return "personal";
    }

}
