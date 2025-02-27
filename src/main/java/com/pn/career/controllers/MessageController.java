package com.pn.career.controllers;

import com.pn.career.models.Message;
import com.pn.career.models.User;
import com.pn.career.responses.MessageResponse;
import com.pn.career.services.IMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/messages")
@RequiredArgsConstructor
public class MessageController {
    private final IMessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody Map<String, Object> payload) {
        Integer senderId = Integer.parseInt(payload.get("senderId").toString());
        Integer recipientId = Integer.parseInt(payload.get("recipientId").toString());
        String content = payload.get("content").toString();

        MessageResponse message = messageService.sendMessage(senderId, recipientId, content);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/conversation")
    public ResponseEntity<List<MessageResponse>> getConversation(
            @RequestParam Integer user1Id,
            @RequestParam Integer user2Id) {
        List<MessageResponse> conversation = messageService.getConservation(user1Id, user2Id);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/contacts/{userId}")
    public ResponseEntity<List<User>> getContacts(@PathVariable Integer userId) {
        List<User> contacts = messageService.getContacts(userId);
        return ResponseEntity.ok(contacts);
    }

    @PutMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<MessageResponse>> getUnreadMessages(@PathVariable Integer userId) {
        List<MessageResponse> unreadMessages = messageService.getUnreadMessages(userId);
        return ResponseEntity.ok(unreadMessages);
    }
}
