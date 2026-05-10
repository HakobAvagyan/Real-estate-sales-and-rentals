package org.example.controller.property;

import lombok.RequiredArgsConstructor;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.service.CommentService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/properties/{propertyId}/comments")
@RequiredArgsConstructor
public class PropertyCommentRestController {

    private final CommentService commentService;
    private final UserService userService;

    private int getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED);
        }
        return userService.findByEmail(authentication.getName()).getId();
    }

    @PostMapping
    public ResponseEntity<Void> addComment(
            @PathVariable int propertyId,
            @RequestParam String comment) {
        int userId = getCurrentUserId();
        commentService.addPublicComment(userId, propertyId, comment);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable int propertyId,
            @PathVariable int commentId) {
        commentService.deleteById(commentId);
        return ResponseEntity.ok().build();
    }
}
