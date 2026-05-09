package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.example.service.CommentService;
import org.example.service.security.SpringUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/delete")
    public String deleteComment(@RequestParam int commentId,
                                @RequestParam int propertyId,
                                @AuthenticationPrincipal SpringUser userPrincipal) {
        commentService.deleteById(commentId);
        return "redirect:/property/details?propertyId=" + propertyId + "#comments";
    }
}