package org.example.service.impl;

import org.example.dto.comment.PropertyCommentViewDto;
import org.example.exception.BusinessException;
import org.example.model.Comment;
import org.example.model.Property;
import org.example.model.User;
import org.example.repository.CommentRepository;
import org.example.repository.PropertyRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock CommentRepository commentRepository;
    @Mock UserRepository userRepository;
    @Mock PropertyRepository propertyRepository;

    @InjectMocks
    CommentServiceImpl commentService;

    private User user(int id, String name, String surname) {
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setSurname(surname);
        return u;
    }

    private Property property(int id) {
        Property p = new Property();
        p.setId(id);
        User owner = user(99, "Owner", "");
        p.setUser(owner);
        return p;
    }

    private Comment comment(int id, String text, User u, Property p) {
        Comment c = new Comment();
        c.setId(id);
        c.setComment(text);
        c.setUser(u);
        c.setProperty(p);
        c.setCreatedAt(LocalDate.now());
        return c;
    }

    @Test
    void addPublicComment_savesCommentSuccessfully() {
        User u = user(1, "Anna", "Smith");
        Property p = property(10);
        when(userRepository.findById(1)).thenReturn(Optional.of(u));
        when(propertyRepository.findById(10)).thenReturn(Optional.of(p));

        commentService.addPublicComment(1, 10, "Great property!");

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_delegatesToAddPublicComment() {
        User u = user(1, "Bob", "Jones");
        Property p = property(5);
        when(userRepository.findById(1)).thenReturn(Optional.of(u));
        when(propertyRepository.findById(5)).thenReturn(Optional.of(p));

        commentService.addComment("Nice listing", 1, 5);

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addPublicComment_throwsWhenTextIsEmpty() {
        assertThrows(BusinessException.class,
                () -> commentService.addPublicComment(1, 10, ""));
        verifyNoInteractions(commentRepository);
    }

    @Test
    void addPublicComment_throwsWhenTextIsBlank() {
        assertThrows(BusinessException.class,
                () -> commentService.addPublicComment(1, 10, "   "));
        verifyNoInteractions(commentRepository);
    }

    @Test
    void addPublicComment_throwsWhenTextTooLong() {
        String longText = "x".repeat(4001);
        assertThrows(BusinessException.class,
                () -> commentService.addPublicComment(1, 10, longText));
        verifyNoInteractions(commentRepository);
    }

    @Test
    void addPublicComment_allowsExactlyMaxLength() {
        String maxText = "x".repeat(4000);
        User u = user(1, "A", "B");
        Property p = property(10);
        when(userRepository.findById(1)).thenReturn(Optional.of(u));
        when(propertyRepository.findById(10)).thenReturn(Optional.of(p));

        assertDoesNotThrow(() -> commentService.addPublicComment(1, 10, maxText));
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addPublicComment_throwsWhenUserNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class,
                () -> commentService.addPublicComment(999, 10, "Hello"));
    }

    @Test
    void addPublicComment_throwsWhenPropertyNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user(1, "A", "B")));
        when(propertyRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class,
                () -> commentService.addPublicComment(1, 999, "Hello"));
    }

    @Test
    void listPublicCommentsForProperty_mapsCommentsToViews() {
        User u = user(1, "John", "Doe");
        Property p = property(5);
        Comment c = comment(10, "Good place", u, p);
        when(commentRepository.findForPropertyWithUserOrderByCreatedAtDesc(5)).thenReturn(List.of(c));

        List<PropertyCommentViewDto> views = commentService.listPublicCommentsForProperty(5);

        assertEquals(1, views.size());
        PropertyCommentViewDto view = views.get(0);
        assertEquals(10, view.getId());
        assertEquals("Good place", view.getText());
        assertEquals("John Doe", view.getAuthorDisplayName());
        assertEquals(1, view.getUserId());
    }

    @Test
    void listPublicCommentsForProperty_usesEmailWhenNoName() {
        User u = user(2, null, null);
        u.setEmail("user@example.com");
        Property p = property(5);
        Comment c = comment(11, "Nice", u, p);
        when(commentRepository.findForPropertyWithUserOrderByCreatedAtDesc(5)).thenReturn(List.of(c));

        List<PropertyCommentViewDto> views = commentService.listPublicCommentsForProperty(5);

        assertEquals("user@example.com", views.get(0).getAuthorDisplayName());
    }

    @Test
    void listPublicCommentsForProperty_fallsBackToUserIdLabel() {
        User u = user(3, null, null);
        u.setEmail(null);
        Property p = property(5);
        Comment c = comment(12, "OK", u, p);
        when(commentRepository.findForPropertyWithUserOrderByCreatedAtDesc(5)).thenReturn(List.of(c));

        List<PropertyCommentViewDto> views = commentService.listPublicCommentsForProperty(5);

        assertEquals("User #3", views.get(0).getAuthorDisplayName());
    }

    @Test
    void getCommentCountsForPropertyIds_returnsEmptyMapForNull() {
        Map<Integer, Long> result = commentService.getCommentCountsForPropertyIds(null);
        assertTrue(result.isEmpty());
        verifyNoInteractions(commentRepository);
    }

    @Test
    void getCommentCountsForPropertyIds_returnsEmptyMapForEmptyList() {
        Map<Integer, Long> result = commentService.getCommentCountsForPropertyIds(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void getCommentCountsForPropertyIds_returnsCountsAndFillsZerosForMissing() {
        when(commentRepository.countGroupedByPropertyIds(anyList()))
                .thenReturn(java.util.Arrays.asList(new Object[][]{{10, 3L}}));

        Map<Integer, Long> result = commentService.getCommentCountsForPropertyIds(List.of(10, 20));

        assertEquals(3L, result.get(10));
        assertEquals(0L, result.get(20));
    }

    @Test
    void deleteById_callsRepository() {
        commentService.deleteById(5);
        verify(commentRepository).deleteById(5);
    }

    @Test
    void findAllByUserId_returnsComments() {
        Comment c = comment(1, "text", user(1, "A", "B"), property(1));
        when(commentRepository.findAllByUser_Id(1)).thenReturn(List.of(c));

        List<Comment> result = commentService.findAllByUserId(1);
        assertEquals(1, result.size());
    }

    @Test
    void findById_returnsComment() {
        Comment c = comment(7, "hello", user(1, "A", "B"), property(1));
        when(commentRepository.findById(7)).thenReturn(Optional.of(c));

        Optional<Comment> result = commentService.findById(7);
        assertTrue(result.isPresent());
        assertEquals("hello", result.get().getComment());
    }

    @Test
    void findAll_returnsAll() {
        when(commentRepository.findAll()).thenReturn(List.of(
                comment(1, "a", user(1, "A", "B"), property(1)),
                comment(2, "b", user(2, "C", "D"), property(2))
        ));

        assertEquals(2, commentService.findAll().size());
    }

    @Test
    void findAllCommentsForSeller_delegatesToRepository() {
        when(commentRepository.findAllCommentsForUserAsSeller(5)).thenReturn(List.of());
        List<Comment> result = commentService.findAllCommentsForSeller(5);
        assertTrue(result.isEmpty());
        verify(commentRepository).findAllCommentsForUserAsSeller(5);
    }
}
