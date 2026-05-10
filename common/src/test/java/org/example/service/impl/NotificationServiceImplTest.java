package org.example.service.impl;

import org.example.dto.notification.NotificationRequestDto;
import org.example.exception.BusinessException;
import org.example.mapper.notification.NotificationRequestMapper;
import org.example.mapper.notification.NotificationResponseMapper;
import org.example.model.Notification;
import org.example.model.User;
import org.example.model.enums.Role;
import org.example.repository.NotificationRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock NotificationRepository notificationRepository;
    @Mock NotificationResponseMapper notificationResponseMapper;
    @Mock NotificationRequestMapper notificationRequestMapper;
    @Mock UserRepository userRepository;

    @InjectMocks
    NotificationServiceImpl notificationService;

    private User user(int id, String email) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setName("Test");
        u.setSurname("User");
        return u;
    }

    private Notification notification(int id, User owner) {
        Notification n = new Notification();
        n.setId(id);
        n.setUser(owner);
        n.setTitle("Test");
        n.setMessage("Message");
        return n;
    }

    private void setAuthentication(String email) {
        var auth = new UsernamePasswordAuthenticationToken(email, null, java.util.List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // --- save ---

    @Test
    void save_persistsNotification() {
        NotificationRequestDto dto = new NotificationRequestDto();
        dto.setUserId(1);
        dto.setTitle("Title");
        dto.setMessage("Msg");

        Notification entity = new Notification();
        when(notificationRequestMapper.toNotification(dto)).thenReturn(entity);

        notificationService.save(dto);

        verify(notificationRepository).save(entity);
    }

    // --- notifyUserBlocked ---

    @Test
    void notifyUserBlocked_savesBlockedNotification() {
        User u = user(1, "user@test.com");
        Notification entity = notification(0, u);
        when(notificationRequestMapper.toNotification(any(NotificationRequestDto.class))).thenReturn(entity);

        notificationService.notifyUserBlocked(u);

        ArgumentCaptor<NotificationRequestDto> captor = ArgumentCaptor.forClass(NotificationRequestDto.class);
        verify(notificationRequestMapper).toNotification(captor.capture());
        NotificationRequestDto captured = captor.getValue();

        assertEquals(1, captured.getUserId());
        assertTrue(captured.getTitle().contains("blocked"));
        verify(notificationRepository).save(entity);
    }

    // --- notifyUserUnblocked ---

    @Test
    void notifyUserUnblocked_savesUnblockedNotification() {
        User u = user(2, "user2@test.com");
        Notification entity = notification(0, u);
        when(notificationRequestMapper.toNotification(any(NotificationRequestDto.class))).thenReturn(entity);

        notificationService.notifyUserUnblocked(u);

        ArgumentCaptor<NotificationRequestDto> captor = ArgumentCaptor.forClass(NotificationRequestDto.class);
        verify(notificationRequestMapper).toNotification(captor.capture());
        NotificationRequestDto captured = captor.getValue();

        assertEquals(2, captured.getUserId());
        assertTrue(captured.getTitle().contains("unblocked"));
        verify(notificationRepository).save(entity);
    }

    // --- createNotificationForUser ---

    @Test
    void createNotificationForUser_savesWithCorrectFields() {
        Notification entity = new Notification();
        when(notificationRequestMapper.toNotification(any(NotificationRequestDto.class))).thenReturn(entity);

        notificationService.createNotificationForUser(5, "Welcome", "Hello there");

        ArgumentCaptor<NotificationRequestDto> captor = ArgumentCaptor.forClass(NotificationRequestDto.class);
        verify(notificationRequestMapper).toNotification(captor.capture());
        NotificationRequestDto dto = captor.getValue();

        assertEquals(5, dto.getUserId());
        assertEquals("Welcome", dto.getTitle());
        assertEquals("Hello there", dto.getMessage());
        verify(notificationRepository).save(entity);
    }

    // --- findById (requires SecurityContext) ---

    @Test
    void findById_throwsWhenNotificationNotFound() {
        when(notificationRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> notificationService.findById(999));
    }

    @Test
    void findById_throwsWhenAccessedByDifferentUser() {
        setAuthentication("other@test.com");
        User owner = user(1, "owner@test.com");
        User other = user(2, "other@test.com");
        Notification n = notification(10, owner);

        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(other));
        when(notificationRepository.findById(10)).thenReturn(Optional.of(n));

        assertThrows(BusinessException.class, () -> notificationService.findById(10));

        SecurityContextHolder.clearContext();
    }

    @Test
    void findById_returnsNotificationWhenOwnerAccesses() {
        setAuthentication("owner@test.com");
        User owner = user(1, "owner@test.com");
        Notification n = notification(10, owner);
        NotificationRequestDto dto = new NotificationRequestDto();
        dto.setUserId(1);
        dto.setTitle("Test");

        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
        when(notificationRepository.findById(10)).thenReturn(Optional.of(n));
        when(notificationRequestMapper.toDto(n)).thenReturn(dto);

        NotificationRequestDto result = notificationService.findById(10);

        assertNotNull(result);
        assertEquals(1, result.getUserId());

        SecurityContextHolder.clearContext();
    }

    // --- deleteById (requires SecurityContext) ---

    @Test
    void deleteById_throwsWhenNotificationNotFound() {
        when(notificationRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> notificationService.deleteById(999));
    }

    @Test
    void deleteById_throwsWhenAccessedByDifferentUser() {
        setAuthentication("other@test.com");
        User owner = user(1, "owner@test.com");
        User other = user(2, "other@test.com");
        Notification n = notification(10, owner);

        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(other));
        when(notificationRepository.findById(10)).thenReturn(Optional.of(n));

        assertThrows(BusinessException.class, () -> notificationService.deleteById(10));

        SecurityContextHolder.clearContext();
    }

    @Test
    void deleteById_deletesWhenOwnerAccesses() {
        setAuthentication("owner@test.com");
        User owner = user(1, "owner@test.com");
        Notification n = notification(10, owner);

        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
        when(notificationRepository.findById(10)).thenReturn(Optional.of(n));

        notificationService.deleteById(10);

        verify(notificationRepository).deleteById(10);

        SecurityContextHolder.clearContext();
    }

    // --- getAllNotificationsByUserId (requires SecurityContext) ---

    @Test
    void getAllNotificationsByUserId_returnsListForCurrentUser() {
        setAuthentication("user@test.com");
        User u = user(1, "user@test.com");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(u));
        when(notificationRepository.findAllByUserId(1)).thenReturn(java.util.List.of());

        var result = notificationService.getAllNotificationsByUserId();

        assertTrue(result.isEmpty());
        verify(notificationRepository).findAllByUserId(1);

        SecurityContextHolder.clearContext();
    }
}
