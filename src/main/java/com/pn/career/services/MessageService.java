package com.pn.career.services;

import com.pn.career.dtos.ConversationDTO;
import com.pn.career.dtos.ConversationDTOCus;
import com.pn.career.models.Employer;
import com.pn.career.models.Message;
import com.pn.career.models.Student;
import com.pn.career.models.User;
import com.pn.career.repositories.MessageRepository;
import com.pn.career.repositories.UserRepository;
import com.pn.career.responses.MessageResponse;
import com.pn.career.utils.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService {
        private final MessageRepository messageRepository;
        private final UserRepository userRepository;
        private final EncryptionUtils encryptionUtils;

        @Override
        public ConversationDTOCus sendMessage(Integer senderId, Integer recipientId, String content) {
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
                ConversationDTOCus conversationDTO = new ConversationDTOCus();
                conversationDTO.setSenderId(senderId);
                conversationDTO.setRecipientId(recipientId);
                conversationDTO.setSenderName(sender instanceof Student
                                ? ((Student) sender).getLastName() + ((Student) sender).getFirstName()
                                : ((Employer) sender).getCompanyName());
                conversationDTO.setSenderAvatar(sender instanceof Student ? ((Student) sender).getProfileImage()
                                : ((Employer) sender).getCompanyLogo());
                conversationDTO.setSenderAddress(sender.getAddress());
                conversationDTO.setRecipientName(recipient instanceof Student
                                ? ((Student) recipient).getLastName() + ((Student) recipient).getFirstName()
                                : ((Employer) recipient).getCompanyName());
                conversationDTO.setRecipientAvatar(
                                recipient instanceof Student ? ((Student) recipient).getProfileImage()
                                                : ((Employer) recipient).getCompanyLogo());
                conversationDTO.setRecipientAddress(recipient.getAddress());
                conversationDTO.setLastMessage(content);
                conversationDTO.setMessageId(Math.toIntExact(message.getId()));
                conversationDTO.setLastMessageAt(message.getSentAt());
                conversationDTO.setRead(false);
                conversationDTO.setLastSenderId(false);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                conversationDTO.setCreatedAt(message.getCreatedAt().format(formatter));
                return conversationDTO;
        }

        @Override
        public Page<MessageResponse> getConservation(Integer user1Id, Integer user2Id, PageRequest pageRequest) {
                User user1 = userRepository.findById(user1Id)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                User user2 = userRepository.findById(user2Id)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                List<Message> messages = messageRepository.findConversation(user1, user2);
                List<MessageResponse> messageResponses = messages.stream()
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
                                        decryptedMessage.setCreatedAt(message.getCreatedAt());
                                        return MessageResponse.fromMessage(decryptedMessage);
                                })
                                .collect(Collectors.toList());
                return new PageImpl<>(messageResponses, pageRequest, messageResponses.size());
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

        @Override
        public Page<ConversationDTO> getConversations(Integer userId, PageRequest pageable) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                List<User> contacts = messageRepository.findContactsByUser(user);

                List<ConversationDTO> conversationDTOs = contacts.stream()
                                .map(contact -> {
                                        List<Message> messages = messageRepository.findConversation(user, contact);

                                        List<Message> sortedMessages = messages.stream()
                                                        .sorted(Comparator.comparing(Message::getSentAt).reversed())
                                                        .collect(Collectors.toList());

                                        Message latestMessage = sortedMessages.isEmpty() ? null : sortedMessages.get(0);

                                        String avatar = null;
                                        String name = null;
                                        Integer messageId = Math.toIntExact(
                                                        latestMessage != null ? latestMessage.getId() : null);
                                        if (contact instanceof Student) {
                                                avatar = ((Student) contact).getProfileImage();
                                                name = ((Student) contact).getFirstName();
                                        } else if (contact instanceof Employer) {
                                                avatar = ((Employer) contact).getCompanyLogo();
                                                name = ((Employer) contact).getCompanyName();
                                        }
                                        boolean isLastSenderCurrentUser = latestMessage != null
                                                        && latestMessage.getSender().equals(user);

                                        String lastMessageContent = null;
                                        if (latestMessage != null) {
                                                lastMessageContent = encryptionUtils
                                                                .decrypt(latestMessage.getContent());
                                        }
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                                        String formattedCreatedAt = latestMessage != null
                                                        ? latestMessage.getCreatedAt().format(formatter)
                                                        : null;

                                        return ConversationDTO.builder()
                                                        .recipientId(contact.getUserId())
                                                        .name(name)
                                                        .avatar(avatar)
                                                        .address(contact.getAddress())
                                                        .lastMessage(lastMessageContent)
                                                        .messageId(messageId)
                                                        .lastMessageAt(latestMessage != null ? latestMessage.getSentAt()
                                                                        : null)
                                                        .read(latestMessage != null ? latestMessage.isRead() : true)
                                                        .lastSenderId(isLastSenderCurrentUser)
                                                        .createdAt(formattedCreatedAt)
                                                        .build();
                                })
                                .sorted(Comparator.comparing(ConversationDTO::getLastMessageAt,
                                                Comparator.nullsLast(Comparator.reverseOrder())))
                                .collect(Collectors.toList());

                int start = (int) pageable.getOffset();
                int end = Math.min((start + pageable.getPageSize()), conversationDTOs.size());
                List<ConversationDTO> pagedConversationDTOs = conversationDTOs.subList(start, end);

                return new PageImpl<>(pagedConversationDTOs, pageable, conversationDTOs.size());
        }
}
