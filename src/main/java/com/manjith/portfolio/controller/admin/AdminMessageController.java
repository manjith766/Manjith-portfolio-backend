package com.manjith.portfolio.controller.admin;

import com.manjith.portfolio.constants.AppConstants;
import com.manjith.portfolio.dto.response.MessageResponseDTO;
import com.manjith.portfolio.dto.response.PageResponseDTO;
import com.manjith.portfolio.dto.response.UnreadCountResponseDTO;
import com.manjith.portfolio.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Messages", description = "Contact inbox management")
public class AdminMessageController {

    private final MessageService messageService;

    @GetMapping("/api/admin/messages")
    @Operation(summary = "List contact messages, optionally filtered by read status")
    public ResponseEntity<PageResponseDTO<MessageResponseDTO>> getMessages(
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_SIZE) int size) {
        log.info("GET /api/admin/messages isRead={}, page={}, size={}", isRead, page, size);
        PageResponseDTO<MessageResponseDTO> result = messageService.getMessages(isRead, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/admin/messages/unread-count")
    @Operation(summary = "Get the count of unread messages (for a dashboard badge)")
    public ResponseEntity<UnreadCountResponseDTO> getUnreadCount() {
        log.info("GET /api/admin/messages/unread-count");
        long count = messageService.getUnreadCount();
        return ResponseEntity.ok(UnreadCountResponseDTO.builder().unreadCount(count).build());
    }

    @PatchMapping("/api/admin/messages/{id}/read")
    @Operation(summary = "Mark a message as read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        log.info("PATCH /api/admin/messages/{}/read", id);
        messageService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/admin/messages/{id}")
    @Operation(summary = "Delete a message")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        log.info("DELETE /api/admin/messages/{}", id);
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}
