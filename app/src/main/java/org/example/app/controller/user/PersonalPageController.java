package org.example.app.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserRequestDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
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
        UserRequestDto user = userService.findById(id);
        if (user == null || !userService.chekUserById(id)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND,id);
        }
        modelMap.addAttribute("user", user);
        modelMap.addAttribute("role", Role.ADMIN);
        return "personal";
    }

}
