package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.SeoResponseDTO;
import com.manjith.portfolio.entity.SeoMetadata;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SeoMetadataMapper {

    SeoResponseDTO toResponseDTO(SeoMetadata seoMetadata);
}
