package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.EducationResponseDTO;
import com.manjith.portfolio.entity.Education;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T17:19:48+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class EducationMapperImpl implements EducationMapper {

    @Override
    public EducationResponseDTO toResponseDTO(Education education) {
        if ( education == null ) {
            return null;
        }

        EducationResponseDTO.EducationResponseDTOBuilder educationResponseDTO = EducationResponseDTO.builder();

        educationResponseDTO.id( education.getId() );
        educationResponseDTO.degree( education.getDegree() );
        educationResponseDTO.institution( education.getInstitution() );
        educationResponseDTO.location( education.getLocation() );
        educationResponseDTO.startDate( education.getStartDate() );
        educationResponseDTO.endDate( education.getEndDate() );
        educationResponseDTO.cgpa( education.getCgpa() );
        educationResponseDTO.description( education.getDescription() );
        educationResponseDTO.displayOrder( education.getDisplayOrder() );

        educationResponseDTO.achievements( mapAchievementTexts(education.getAchievements()) );

        return educationResponseDTO.build();
    }
}
