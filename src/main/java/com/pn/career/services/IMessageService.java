package com.pn.career.services;

import com.pn.career.dtos.ConservationDTO;
import com.pn.career.dtos.MessageDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IMessageService {
   MessageDTO savMessageAndSend(MessageDTO messageDTO, Integer userId);
   List<ConservationDTO> getConservations(Integer userId);
   Page<MessageDTO> getMessagesForConservation(Integer partnerId, Integer userId, Integer page, Integer size);
   void markAsRead(Long messageId, Integer userId);
}
