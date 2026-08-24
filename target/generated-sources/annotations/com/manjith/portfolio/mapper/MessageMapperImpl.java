package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.MessageResponseDTO;
import com.manjith.portfolio.entity.Message;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T17:19:48+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class MessageMapperImpl implements MessageMapper {

    @Override
    public MessageResponseDTO toResponseDTO(Message message) {
        if ( message == null ) {
            return null;
        }

        MessageResponseDTO.MessageResponseDTOBuilder messageResponseDTO = MessageResponseDTO.builder();

        messageResponseDTO.id( message.getId() );
        messageResponseDTO.name( message.getName() );
        messageResponseDTO.email( message.getEmail() );
        messageResponseDTO.subject( message.getSubject() );
        messageResponseDTO.message( message.getMessage() );
        messageResponseDTO.isRead( message.getIsRead() );
        messageResponseDTO.createdAt( message.getCreatedAt() );

        return messageResponseDTO.build();
    }
}
