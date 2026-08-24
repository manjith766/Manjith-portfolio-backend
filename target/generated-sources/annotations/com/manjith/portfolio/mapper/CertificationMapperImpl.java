package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.CertificationResponseDTO;
import com.manjith.portfolio.entity.Certification;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T17:19:48+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class CertificationMapperImpl implements CertificationMapper {

    @Override
    public CertificationResponseDTO toResponseDTO(Certification certification) {
        if ( certification == null ) {
            return null;
        }

        CertificationResponseDTO.CertificationResponseDTOBuilder certificationResponseDTO = CertificationResponseDTO.builder();

        certificationResponseDTO.id( certification.getId() );
        certificationResponseDTO.title( certification.getTitle() );
        certificationResponseDTO.issuer( certification.getIssuer() );
        certificationResponseDTO.issueDate( certification.getIssueDate() );
        certificationResponseDTO.credentialUrl( certification.getCredentialUrl() );
        certificationResponseDTO.imageUrl( certification.getImageUrl() );
        certificationResponseDTO.displayOrder( certification.getDisplayOrder() );

        return certificationResponseDTO.build();
    }
}
