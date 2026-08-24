package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.CertificationResponseDTO;
import com.manjith.portfolio.entity.Certification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CertificationMapper {

    CertificationResponseDTO toResponseDTO(Certification certification);
}
