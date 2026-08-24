package com.manjith.portfolio.service.impl;

import com.manjith.portfolio.constants.AppConstants;
import com.manjith.portfolio.dto.request.MessageRequestDTO;
import com.manjith.portfolio.dto.response.MessageResponseDTO;
import com.manjith.portfolio.dto.response.PageResponseDTO;
import com.manjith.portfolio.entity.Message;
import com.manjith.portfolio.exception.ErrorCode;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.MessageMapper;
import com.manjith.portfolio.repository.MessageRepository;
import com.manjith.portfolio.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * QUERY COUNT PER ENDPOINT: this is a flat entity, no relations — every
 * operation is exactly 1 query (2 for markAsRead: the atomic UPDATE
 * itself, no read-first).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public MessageResponseDTO submitMessage(MessageRequestDTO requestDTO) {
        log.info("API entry: submitMessage from email={}", requestDTO.getEmail());

        Message message = Message.builder()
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .subject(requestDTO.getSubject())
                .message(requestDTO.getMessage())
                .isRead(false)
                .build();

        Message saved = messageRepository.save(message);
        log.debug("Persisted message id={}", saved.getId());

        log.info("API exit: submitMessage created id={}", saved.getId());
        return messageMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<MessageResponseDTO> getMessages(Boolean isRead, int page, int size) {
        log.info("API entry: getMessages isRead={}, page={}, size={}", isRead, page, size);

        int safePage = Math.max(page, AppConstants.DEFAULT_PAGE_NUMBER);
        int safeSize = size <= 0 ? AppConstants.DEFAULT_PAGE_SIZE : Math.min(size, AppConstants.MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Message> messagePage = isRead == null
                ? messageRepository.findAllByOrderByCreatedAtDesc(pageable)
                : messageRepository.findAllByIsReadOrderByCreatedAtDesc(isRead, pageable);

        Page<MessageResponseDTO> dtoPage = messagePage.map(messageMapper::toResponseDTO);

        log.info("API exit: getMessages returning {} of {} total", dtoPage.getNumberOfElements(), dtoPage.getTotalElements());
        return PageResponseDTO.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        log.debug("Fetching unread message count");
        return messageRepository.countByIsReadFalse();
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        log.info("API entry: markAsRead id={}", id);

        int updated = messageRepository.markAsReadById(id);
        if (updated == 0) {
            log.warn("Message not found for markAsRead, id={}", id);
            throw new ResourceNotFoundException(ErrorCode.MESSAGE_NOT_FOUND, "No message found with id: " + id);
        }

        log.info("API exit: markAsRead marked id={}", id);
    }

    @Override
    @Transactional
    public void deleteMessage(Long id) {
        log.info("API entry: deleteMessage id={}", id);

        if (!messageRepository.existsById(id)) {
            log.warn("Message not found for delete, id={}", id);
            throw new ResourceNotFoundException(ErrorCode.MESSAGE_NOT_FOUND, "No message found with id: " + id);
        }

        messageRepository.deleteById(id);
        log.info("API exit: deleteMessage deleted id={}", id);
    }
}
