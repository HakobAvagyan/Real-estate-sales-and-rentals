package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.example.app.service.security.SpringUser;
import org.example.model.enums.Role;
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
        if (userPrincipal == null) {
            return "home";
        }
        switch (userPrincipal.getUser().getRole()){
            case Role.ADMIN -> {
                return "redirect:/admin/home";
            }
            case Role.USER -> {
                return "redirect:/user/home";
            }
            case Role.MANAGER -> {
                    return "redirect:/manager/home";
            }
            case Role.CUSTOMER ->  {
                return "redirect:/customer/home";
            }
            default -> {;
                return "home";
            }
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
