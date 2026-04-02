package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.example.exception.ErrorCode;
import org.example.model.User;
import org.example.service.UserService;
import org.example.service.security.SpringUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class MainController {

    @Value("${system.upload.images.directory.path}")
    private String imageDirectoryPath;

    @GetMapping("/")
    public String mainPage() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String homePage(@AuthenticationPrincipal SpringUser userPrincipal) {
        User user = userPrincipal != null ? userPrincipal.getUser() : null;
        if (user == null) {
            return "home";
        }
        if(user.isBlocked()){
            return "redirect:/loginPage?msg=" + ErrorCode.PROFILE_IS_BLOCKED.format(user.getEmail());
        }
        switch (user.getRole()){
            case ADMIN: return "redirect:/admin/home";
            case USER: return "redirect:/user/home";
            case MANAGER: return "redirect:/manager/home";
            case CUSTOMER: return "redirect:/customer/home";
            default: return "home";
        }

    }

    @GetMapping("/image/get")
    public @ResponseBody byte[] getImage(@RequestParam("pic") String picName) {
        File file = new File(imageDirectoryPath + picName);
        if (file.exists()) {
            try {
                return FileUtils.readFileToByteArray(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
