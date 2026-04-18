package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.example.dto.location.LocationDto;
import org.example.dto.property.PropertyResponseDto;
import org.example.exception.ErrorCode;
import org.example.model.User;
import org.example.model.enums.PropertyStatus;
import org.example.service.LocationService;
import org.example.service.PaymentService;
import org.example.service.PropertyService;
import org.example.service.UserService;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    @Value("${system.upload.images.directory.path}")
    private String imageDirectoryPath;
    private final PropertyService propertyService;
    private final LocationService locationService;
    private final UserService userService;
    private final PaymentService paymentService;

    @GetMapping("/")
    public String mainPage() {
        return "redirect:/home";
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

        if (user != null && user.isBlocked()) {
            return "redirect:/loginPage?msg=" + ErrorCode.PROFILE_IS_BLOCKED.format(user.getEmail());
        }

        List<PropertyResponseDto> properties = propertyService.findAll();
        modelMap.addAttribute("properties", properties);
        Map<Integer, LocationDto> locationMap = locationService.getAll()
                .stream().collect(Collectors.toMap(LocationDto::getId, l -> l));
        modelMap.addAttribute("locationMap", locationMap);
        modelMap.addAttribute("urgentPropertyIds", paymentService.getActiveUrgentPropertyIds());

        if (user != null) {
            List<Integer> sellerIds = properties.stream()
                    .filter(p -> p.getStatus() == PropertyStatus.FOR_SALE)
                    .map(PropertyResponseDto::getUserId)
                    .distinct()
                    .toList();
            modelMap.addAttribute("sellerPhoneMap", userService.getSellerPhoneMap(sellerIds));

            modelMap.addAttribute("currentUser", user);
            String dashboardUrl = switch (user.getRole()) {
                case ADMIN    -> "/admin/home";
                case USER     -> "/user/home";
                case MANAGER  -> "/manager/home";
            };
            modelMap.addAttribute("dashboardUrl", dashboardUrl);
        }

        return "home";
    }

    @GetMapping("/image/get")
    public @ResponseBody ResponseEntity<byte[]> getImage(
            @RequestParam("pic") String picName,
            @AuthenticationPrincipal SpringUser springUser) {
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
                log.error("Failed to read image file: {} user email: {}", e.getMessage(), springUser.getUser().getEmail());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.notFound().build();
    }

}
