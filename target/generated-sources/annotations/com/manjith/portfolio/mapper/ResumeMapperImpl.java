package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.ResumeResponseDTO;
import com.manjith.portfolio.entity.Resume;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T17:19:48+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ResumeMapperImpl implements ResumeMapper {

    @Override
    public ResumeResponseDTO toResponseDTO(Resume resume) {
        if ( resume == null ) {
            return null;
        }

        ResumeResponseDTO.ResumeResponseDTOBuilder resumeResponseDTO = ResumeResponseDTO.builder();

        resumeResponseDTO.id( resume.getId() );
        resumeResponseDTO.fileUrl( resume.getFileUrl() );
        resumeResponseDTO.fileName( resume.getFileName() );
        resumeResponseDTO.version( resume.getVersion() );
        resumeResponseDTO.isActive( resume.getIsActive() );
        resumeResponseDTO.uploadedAt( resume.getUploadedAt() );

        return resumeResponseDTO.build();
    }
}
