package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.SeoResponseDTO;
import com.manjith.portfolio.entity.SeoMetadata;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T17:19:48+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class SeoMetadataMapperImpl implements SeoMetadataMapper {

    @Override
    public SeoResponseDTO toResponseDTO(SeoMetadata seoMetadata) {
        if ( seoMetadata == null ) {
            return null;
        }

        SeoResponseDTO.SeoResponseDTOBuilder seoResponseDTO = SeoResponseDTO.builder();

        seoResponseDTO.id( seoMetadata.getId() );
        seoResponseDTO.pagePath( seoMetadata.getPagePath() );
        seoResponseDTO.metaTitle( seoMetadata.getMetaTitle() );
        seoResponseDTO.metaDescription( seoMetadata.getMetaDescription() );
        seoResponseDTO.ogImageUrl( seoMetadata.getOgImageUrl() );
        seoResponseDTO.canonicalUrl( seoMetadata.getCanonicalUrl() );
        seoResponseDTO.updatedAt( seoMetadata.getUpdatedAt() );

        return seoResponseDTO.build();
    }
}
