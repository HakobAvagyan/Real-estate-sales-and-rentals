package org.example.app.controller;

import org.example.model.Comment;
import org.example.service.UserService;
//import org.example.service.PropertyService;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;
import org.example.service.CommentService;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.example.mapper.UserRegisterMapper;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
//    private final PropertyService propertyService;

    @GetMapping("/comments")
    public String getComments(ModelMap modelMap) {
        modelMap.addAttribute("comments", commentService.findAll());
        return "comments";
    }

    @PostMapping("/comments/add")
    public String addComment(@RequestParam String comment, @RequestParam int userId, @RequestParam int propertyId, ModelMap modelMap) {
        Comment c = new Comment();
        c.setComment(comment);
        c.setUser(userService.findById(userId).map(UserRegisterMapper::toUser).orElseThrow(() -> new IllegalArgumentException("User not found")));
//        c.setProperty(propertyService.findById(propertyId).map(PropertyMapper::toProperty).orElseThrow(() -> new IllegalArgumentException("Property not found")));
        commentService.save(c);
        return "redirect:/comments/by-property?propertyId=" + propertyId;
    }

    @GetMapping("/comments/by-property")
    public String getCommentsByProperty(@RequestParam int propertyId, ModelMap modelMap) {
        modelMap.addAttribute("comments", commentService.findAllByPropertyId(propertyId));
        return "comments";
    }

    @GetMapping("/comments/delete")
    public String deleteComment(@RequestParam int commentId, ModelMap modelMap) {
        commentService.deleteById(commentId);
        return "redirect:/comments/by-property?propertyId=" + commentId;
    }
}