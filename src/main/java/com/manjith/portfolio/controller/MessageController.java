package com.manjith.portfolio.controller;

import com.manjith.portfolio.dto.request.MessageRequestDTO;
import com.manjith.portfolio.dto.response.MessageResponseDTO;
import com.manjith.portfolio.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Contact", description = "Public contact form submission")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/api/messages")
    @Operation(summary = "Submit a contact form message")
    public ResponseEntity<MessageResponseDTO> submitMessage(@Valid @RequestBody MessageRequestDTO requestDTO) {
        log.info("POST /api/messages from email={}", requestDTO.getEmail());
        MessageResponseDTO result = messageService.submitMessage(requestDTO);
        return ResponseEntity.status(201).body(result);
    }
}
