package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.SocialLinkResponseDTO;
import com.manjith.portfolio.entity.SocialLink;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T17:19:48+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class SocialLinkMapperImpl implements SocialLinkMapper {

    @Override
    public SocialLinkResponseDTO toResponseDTO(SocialLink socialLink) {
        if ( socialLink == null ) {
            return null;
        }

        SocialLinkResponseDTO.SocialLinkResponseDTOBuilder socialLinkResponseDTO = SocialLinkResponseDTO.builder();

        socialLinkResponseDTO.id( socialLink.getId() );
        socialLinkResponseDTO.platform( socialLink.getPlatform() );
        socialLinkResponseDTO.url( socialLink.getUrl() );
        socialLinkResponseDTO.icon( socialLink.getIcon() );
        socialLinkResponseDTO.displayOrder( socialLink.getDisplayOrder() );

        return socialLinkResponseDTO.build();
    }
}
