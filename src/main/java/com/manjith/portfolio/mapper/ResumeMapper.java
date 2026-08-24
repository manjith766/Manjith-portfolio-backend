package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.ResumeResponseDTO;
import com.manjith.portfolio.entity.Resume;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResumeMapper {

    ResumeResponseDTO toResponseDTO(Resume resume);
}
