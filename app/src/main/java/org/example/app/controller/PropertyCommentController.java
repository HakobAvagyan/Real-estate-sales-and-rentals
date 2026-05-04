package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.example.exception.BusinessException;
import org.example.service.CommentService;
import org.example.service.security.SpringUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PropertyCommentController {

    private final CommentService commentService;

    @PostMapping("/property/comment")
    public String addPropertyComment(@AuthenticationPrincipal SpringUser userPrincipal,
                                     @RequestParam int propertyId,
                                     @RequestParam String comment,
                                     RedirectAttributes redirectAttributes) {
        if (userPrincipal == null) {
            return "redirect:/loginPage";
        }
        try {
            commentService.addPublicComment(userPrincipal.getUser().getId(), propertyId, comment);
            redirectAttributes.addFlashAttribute("commentSuccess", true);
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("commentError", ex.getMessage());
        }
        return "redirect:/property/details?propertyId=" + propertyId + "#comments";
    }
}
