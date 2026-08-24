package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.MessageResponseDTO;
import com.manjith.portfolio.entity.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageResponseDTO toResponseDTO(Message message);
}
