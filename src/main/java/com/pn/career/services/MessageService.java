package com.pn.career.services;
import com.pn.career.models.Message;
import com.pn.career.models.User;
import com.pn.career.repositories.MessageRepository;
import com.pn.career.repositories.UserRepository;
import com.pn.career.responses.MessageResponse;
import com.pn.career.utils.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService{
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final EncryptionUtils encryptionUtils;

    @Override
    public MessageResponse sendMessage(Integer senderId, Integer recipientId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
        String encryptedContent = encryptionUtils.encrypt(content);
        Message message = Message.builder()
                .sender(sender)
                .recipient(recipient)
                .content(encryptedContent)
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();

        messageRepository.save(message);
        Message messageRes= Message.builder()
                .id(message.getId())
                .sender(message.getSender())
                .recipient(message.getRecipient())
                .content(content) // Dùng nội dung gốc thay vì phải giải mã
                .sentAt(message.getSentAt())
                .isRead(message.isRead())
                .readAt(message.getReadAt())
                .build();

        return MessageResponse.fromMessage(messageRes);
    }

    @Override
    public List<MessageResponse> getConservation(Integer user1Id, Integer user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Message> messages = messageRepository.findConversation(user1, user2);
        return messages.stream()
                .map(message -> {
                    // Giải mã nội dung tin nhắn
                    String decryptedContent = encryptionUtils.decrypt(message.getContent());
                    Message decryptedMessage = new Message();
                    decryptedMessage.setId(message.getId());
                    decryptedMessage.setSender(message.getSender());
                    decryptedMessage.setRecipient(message.getRecipient());
                    decryptedMessage.setContent(decryptedContent);
                    decryptedMessage.setSentAt(message.getSentAt());
                    decryptedMessage.setRead(message.isRead());
                    decryptedMessage.setReadAt(message.getReadAt());

                    return MessageResponse.fromMessage(decryptedMessage);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getContacts(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return messageRepository.findContactsByUser(user);
    }

    @Override
    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setRead(true);
        message.setReadAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    @Override
    public List<MessageResponse> getUnreadMessages(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Message> messages = messageRepository.findUnreadMessagesByRecipient(user);
        return messages.stream()
                .map(message -> {
                    String decryptedContent = encryptionUtils.decrypt(message.getContent());
                    Message decryptedMessage = new Message();
                    decryptedMessage.setId(message.getId());
                    decryptedMessage.setSender(message.getSender());
                    decryptedMessage.setRecipient(message.getRecipient());
                    decryptedMessage.setContent(decryptedContent);
                    decryptedMessage.setSentAt(message.getSentAt());
                    decryptedMessage.setRead(message.isRead());
                    decryptedMessage.setReadAt(message.getReadAt());

                    return MessageResponse.fromMessage(decryptedMessage);
                })
                .collect(Collectors.toList());
    }
}
