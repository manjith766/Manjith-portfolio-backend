package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.VisitorLogResponseDTO;
import com.manjith.portfolio.entity.VisitorLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VisitorLogMapper {

    VisitorLogResponseDTO toResponseDTO(VisitorLog visitorLog);
}
