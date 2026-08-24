package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.MessageRequestDTO;
import com.manjith.portfolio.dto.response.MessageResponseDTO;
import com.manjith.portfolio.dto.response.PageResponseDTO;

public interface MessageService {

    MessageResponseDTO submitMessage(MessageRequestDTO requestDTO);

    PageResponseDTO<MessageResponseDTO> getMessages(Boolean isRead, int page, int size);

    long getUnreadCount();

    void markAsRead(Long id);

    void deleteMessage(Long id);
}
