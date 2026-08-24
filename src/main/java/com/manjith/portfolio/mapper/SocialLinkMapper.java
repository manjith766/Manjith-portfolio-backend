package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.SocialLinkResponseDTO;
import com.manjith.portfolio.entity.SocialLink;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SocialLinkMapper {

    SocialLinkResponseDTO toResponseDTO(SocialLink socialLink);
}
