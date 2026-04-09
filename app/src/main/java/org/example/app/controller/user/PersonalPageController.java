package org.example.app.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserResponseDto;
import org.example.model.enums.Role;
import org.example.service.UserService;
import org.example.service.security.SpringUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PersonalPageController {

    private final UserService userService;

    @GetMapping("/personalPage")
    public String personalPage(
            @RequestParam("id") int id,
            @AuthenticationPrincipal SpringUser springUser,
            ModelMap modelMap) {
        UserResponseDto user = userService.findById(id);
        modelMap.addAttribute("user", user);
        modelMap.addAttribute("role", Role.ADMIN);
        boolean ownProfile = springUser != null && springUser.getUser().getId() == id;
        modelMap.addAttribute("ownProfile", ownProfile);
        return "personal";
    }

}
