package com.pn.career.controllers;

import com.pn.career.models.Message;
import com.pn.career.responses.MessageResponse;
import com.pn.career.services.IMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final IMessageService messageService;
    @MessageMapping("/chat")
    public void processMessage(@Payload Map<String, Object> payload) {
        Integer senderId = Integer.parseInt(payload.get("senderId").toString());
        Integer recipientId = Integer.parseInt(payload.get("recipientId").toString());
        String content = payload.get("content").toString();
        log.info("Received message: sender={}, recipient={}, content={}",
                senderId, recipientId, content);
        MessageResponse savedMessage = messageService.sendMessage(senderId, recipientId, content);
        log.info("Sending message to user: {}", recipientId);

        messagingTemplate.convertAndSendToUser(
                recipientId.toString(),
                "/queue/messages",
                savedMessage
        );
    }
}
