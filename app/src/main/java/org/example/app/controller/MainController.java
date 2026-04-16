package org.example.app.controller;

import org.apache.commons.io.FileUtils;
import lombok.RequiredArgsConstructor;
import org.example.dto.property.PropertyResponseDto;
import org.example.exception.ErrorCode;
import org.example.model.User;
import org.example.service.PropertyService;
import org.example.service.security.SpringUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
public class MainController {

    @Value("${system.upload.images.directory.path}")
    private String imageDirectoryPath;
    private final PropertyService propertyService;

    @GetMapping("/")
    public String mainPage() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String indexPage(ModelMap modelMap) {
        java.util.List<PropertyResponseDto> properties = propertyService.findAll();
        modelMap.addAttribute("properties", properties);
        return "index";
    }

    @GetMapping("/home")
    public String homePage(@AuthenticationPrincipal SpringUser userPrincipal, ModelMap modelMap) {
        User user = userPrincipal != null ? userPrincipal.getUser() : null;
        if (user == null) {
            modelMap.addAttribute("properties", propertyService.findAll());
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
    public @ResponseBody ResponseEntity<byte[]> getImage(@RequestParam("pic") String picName) {
        Path basePath = Paths.get(imageDirectoryPath).normalize();
        Path requestedPath = basePath.resolve(picName).normalize();
        if (!requestedPath.startsWith(basePath)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        File file = requestedPath.toFile();
        if (file.exists()) {
            try {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(FileUtils.readFileToByteArray(file));
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.notFound().build();
    }

}
