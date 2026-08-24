package com.manjith.portfolio.service;

import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.MessageMapper;
import com.manjith.portfolio.repository.MessageRepository;
import com.manjith.portfolio.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void markAsRead_throwsResourceNotFoundException_whenNoRowsUpdated() {
        when(messageRepository.markAsReadById(99L)).thenReturn(0);

        assertThatThrownBy(() -> messageService.markAsRead(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteMessage_throwsResourceNotFoundException_whenNotExists() {
        when(messageRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> messageService.deleteMessage(42L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(messageRepository, never()).deleteById(any());
    }
}
