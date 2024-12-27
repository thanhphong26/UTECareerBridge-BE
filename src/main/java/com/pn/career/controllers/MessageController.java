package com.pn.career.controllers;

import com.pn.career.dtos.ConservationDTO;
import com.pn.career.dtos.MessageDTO;
import com.pn.career.dtos.TypingDTO;
import com.pn.career.services.IMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/messages")
@RequiredArgsConstructor
public class MessageController {
    private final IMessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/chat")
    public void sendMessage(@Payload MessageDTO messageDTO, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        MessageDTO savedMessage = messageService.savMessageAndSend(messageDTO, userId);

        // Send to recipient
        messagingTemplate.convertAndSendToUser(
                String.valueOf(savedMessage.getRecipientId()),
                "/queue/messages",
                savedMessage
        );
    }

    @MessageMapping("/typing")
    public void typingIndicator(@Payload TypingDTO typingDTO) {
        messagingTemplate.convertAndSendToUser(
                typingDTO.getRecipientId().toString(),
                "/queue/typing",
                typingDTO
        );
    }

    @GetMapping("/conversations")
    public List<ConservationDTO> getConversations(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            Long userIdLong = jwt.getClaim("userId");
            Integer userId = userIdLong != null ? userIdLong.intValue() : null;
            return messageService.getConservations(userId);
        }
        return null;
    }

    @GetMapping("/conservations/{partnerId}")
    public Page<MessageDTO> getMessages(@PathVariable Integer partnerId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size,
                                        Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        return messageService.getMessagesForConservation(partnerId, userId, page, size);
    }

    @PutMapping("/{messageId}/read")
    public void markAsRead(@PathVariable Long messageId, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userIdLong = jwt.getClaim("userId");
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        messageService.markAsRead(messageId, userId);
    }
}
