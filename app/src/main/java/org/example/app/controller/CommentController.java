package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.CommentDto;
import org.example.mapper.comment.CommentMapper;
import org.example.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @GetMapping("/comments")
    public String getComments(ModelMap modelMap) {
        modelMap.addAttribute("comments", commentService.findAll());
        return "comments";
    }

    @PostMapping("/comments/add")
    public String addComment(@RequestParam String comment, @RequestParam int userId, @RequestParam int propertyId, ModelMap modelMap) {
        CommentDto commentDto = new CommentDto();
        commentDto.setComment(comment);
        commentDto.setUserId(userId);
        commentDto.setPropertyId(propertyId);
        /// TODO: validation and error handling from test conflict 2
        commentService.save(commentMapper.toEntity(commentDto));
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