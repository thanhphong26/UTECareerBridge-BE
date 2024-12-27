package com.pn.career.services;

import com.pn.career.dtos.ConservationDTO;
import com.pn.career.dtos.ConversationDTO;
import com.pn.career.dtos.MessageDTO;
import com.pn.career.dtos.ReadReceiptDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.Message;
import com.pn.career.models.User;
import com.pn.career.repositories.MessageRepository;
import com.pn.career.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService{
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    @Override
    public MessageDTO savMessageAndSend(MessageDTO messageDTO, Integer userId) {
        User sender = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng"));
        User recipient = userRepository.findById(Math.toIntExact(messageDTO.getRecipientId())).orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng"));

        Message message = Message.builder()
                .sender(sender)
                .recipient(recipient)
                .content(messageDTO.getContent())
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();
        messageRepository.save(message);
        MessageDTO savedMessageDTO = MessageDTO.builder()
                .id(message.getId())
                .senderId((long) message.getSender().getUserId())
                .senderName(message.getSender().getFirstName())
                .recipientId((long) message.getRecipient().getUserId())
                .recipientName(message.getRecipient().getFirstName())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .read(message.isRead())
                .readAt(message.getReadAt())
                .build();
        messagingTemplate.convertAndSendToUser(
                String.valueOf(recipient.getUserId()),
                "/queue/messages",
                savedMessageDTO
        );
        return savedMessageDTO;
    }

    @Override
    public List<ConservationDTO> getConservations(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng"));
        return messageRepository.findConversationsForUser(user.getUserId());
    }

    @Override
    public Page<MessageDTO> getMessagesForConservation(Integer partnerId, Integer userId, Integer page, Integer size) {
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng"));
        User partner = userRepository.findById(partnerId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));
        Page<Message> messages = messageRepository.findMessagesBetweenUsers(currentUser.getUserId(), partner.getUserId(), pageable);
        return messages.map(MessageDTO::from);
    }

    @Override
    public void markAsRead(Long messageId, Integer userId) {
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("Không tìm thấy người dùng"));
        Message message = messageRepository.findById(messageId).orElseThrow();

        if (message.getRecipient().getUserId()==(currentUser.getUserId()) && !message.isRead()) {
            message.setRead(true);
            message.setReadAt(LocalDateTime.now());
            messageRepository.save(message);

            messagingTemplate.convertAndSendToUser(
                    String.valueOf(message.getSender().getUserId()),
                    "/queue/read-receipts",
                    ReadReceiptDTO.builder()
                            .messageId(messageId)
                            .readAt(message.getReadAt())
                            .conversationId(Long.valueOf(message.getSender().getUserId()))
                            .build()
            );
        }
    }

}
