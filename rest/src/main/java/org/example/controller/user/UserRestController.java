package org.example.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.dto.user.UserResponseDto;
import org.example.dto.user.UserUpdateDto;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> findAll() {
        return userService.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteById(@PathVariable Integer id) {
        userService.deleteById(id);
    }

    @PostMapping(value = "/update" , consumes = "multipart/form-data")
    public UserUpdateDto update(@ModelAttribute UserUpdateDto userUpdateDto,
                                @RequestParam(value = "pic", required = false) MultipartFile multipartFile) {
        return userService.update(userUpdateDto, multipartFile);
    }


}
