package com.pn.career.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pn.career.dtos.ConversationDTO;
import com.pn.career.dtos.ConversationDTOCus;
import com.pn.career.responses.MessageResponse;
import com.pn.career.services.IMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
@Controller
@Slf4j
@RequiredArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final IMessageService messageService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConversionService conversionService;

    @MessageMapping("/chat")
    public void processMessage(@Payload Map<String, Object> payload) {
        Integer senderId = Integer.parseInt(payload.get("senderId").toString());
        Integer recipientId = Integer.parseInt(payload.get("recipientId").toString());
        String content = payload.get("content").toString();
        String conversationId;
        if (senderId < recipientId) {
            conversationId = senderId + "-" + recipientId;
        } else {
            conversationId = recipientId + "-" + senderId;
        }
        ConversationDTOCus savedMessage = messageService.sendMessage(senderId, recipientId, content);
        MessageResponse messageResponse = convertToMessageResponse(savedMessage, true);
        MessageResponse messageForRecipient = convertToMessageResponse(savedMessage, false);
        MessageResponse messageForSender = convertToMessageResponse(savedMessage, true);

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId,
                messageResponse
        );


        messagingTemplate.convertAndSend(
                "/topic/chat-list/" + senderId,
                savedMessage
        );

        messagingTemplate.convertAndSend(
                "/topic/chat-list/" + recipientId,
                savedMessage
        );
    }
    private MessageResponse convertToMessageResponse(ConversationDTOCus dto, boolean isSender) {
        MessageResponse response = new MessageResponse();
            response.setId(Long.valueOf(dto.getMessageId()));
            response.setSenderId(dto.getRecipientId());
            response.setSenderName(dto.getSenderName());
            response.setRecipientId(dto.getRecipientId());
            response.setRecipientName(dto.getRecipientName());
            response.setContent(dto.getLastMessage());
            response.setSentAt(dto.getLastMessageAt());
            response.setRead(dto.isRead());
            LocalDate today = LocalDate.now();
            LocalTime time = LocalTime.parse(dto.getCreatedAt(), DateTimeFormatter.ofPattern("HH:mm"));
            response.setCreatedAt(LocalDateTime.of(today, time));
            response.setFromSelf(isSender);
        return response;
    }
}
