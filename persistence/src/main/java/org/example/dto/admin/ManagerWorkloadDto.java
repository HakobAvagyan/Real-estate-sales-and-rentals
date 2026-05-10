package org.example.dto.admin;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ManagerWorkloadDto {
    int managerId;
    String displayName;
    String email;
    long messagesSent;
    long chatsHandled;
    LocalDateTime lastMessageAt;
}
