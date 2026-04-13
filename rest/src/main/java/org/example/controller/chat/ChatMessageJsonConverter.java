package org.example.controller.chat;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.dto.chat.SendChatMessagePayload;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageJsonConverter {

    public SendChatMessagePayload convert(JsonNode body, Integer fallbackConversationId) {
        if (body == null || body.isNull()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_BODY);
        }

        Integer conversationIdFromBody = extractConversationId(body);
        Integer effectiveConversationId = resolveConversationId(conversationIdFromBody, fallbackConversationId);

        String text = extractText(body);
        if (text == null || text.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_BODY);
        }

        return new SendChatMessagePayload(effectiveConversationId, text.trim());
    }

    private Integer resolveConversationId(Integer conversationIdFromBody, Integer fallbackConversationId) {
        if (conversationIdFromBody != null && fallbackConversationId != null
                && !conversationIdFromBody.equals(fallbackConversationId)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_BODY);
        }
        Integer id = conversationIdFromBody != null ? conversationIdFromBody : fallbackConversationId;
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_BODY);
        }
        return id;
    }

    private Integer extractConversationId(JsonNode body) {
        Integer direct = asPositiveInt(body.get("conversationId"));
        if (direct != null) {
            return direct;
        }
        Integer snakeCase = asPositiveInt(body.get("conversation_id"));
        if (snakeCase != null) {
            return snakeCase;
        }
        JsonNode conversationNode = body.get("conversation");
        if (conversationNode != null && !conversationNode.isNull()) {
            return asPositiveInt(conversationNode.get("id"));
        }
        return null;
    }

    private String extractText(JsonNode body) {
        String text = asText(body.get("text"));
        if (text != null) {
            return text;
        }
        String message = asText(body.get("message"));
        if (message != null) {
            return message;
        }
        return asText(body.get("body"));
    }

    private Integer asPositiveInt(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isInt() || node.isLong()) {
            int value = node.asInt();
            return value > 0 ? value : null;
        }
        if (node.isTextual()) {
            try {
                int value = Integer.parseInt(node.asText().trim());
                return value > 0 ? value : null;
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private String asText(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isTextual()) {
            return node.asText();
        }
        return node.isValueNode() ? node.asText(null) : null;
    }
}
