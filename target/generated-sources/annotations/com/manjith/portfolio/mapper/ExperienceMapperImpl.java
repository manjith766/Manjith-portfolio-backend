package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.ExperienceResponseDTO;
import com.manjith.portfolio.entity.Experience;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T17:19:48+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ExperienceMapperImpl implements ExperienceMapper {

    @Autowired
    private CommonMapper commonMapper;

    @Override
    public ExperienceResponseDTO toResponseDTO(Experience experience) {
        if ( experience == null ) {
            return null;
        }

        ExperienceResponseDTO.ExperienceResponseDTOBuilder experienceResponseDTO = ExperienceResponseDTO.builder();

        experienceResponseDTO.skills( commonMapper.toSkillSummaryDTOList( experience.getSkills() ) );
        experienceResponseDTO.id( experience.getId() );
        experienceResponseDTO.companyName( experience.getCompanyName() );
        experienceResponseDTO.role( experience.getRole() );
        experienceResponseDTO.location( experience.getLocation() );
        experienceResponseDTO.startDate( experience.getStartDate() );
        experienceResponseDTO.endDate( experience.getEndDate() );
        experienceResponseDTO.isCurrent( experience.getIsCurrent() );
        experienceResponseDTO.description( experience.getDescription() );
        experienceResponseDTO.displayOrder( experience.getDisplayOrder() );
        experienceResponseDTO.createdAt( experience.getCreatedAt() );
        experienceResponseDTO.updatedAt( experience.getUpdatedAt() );

        experienceResponseDTO.responsibilities( mapResponsibilityTexts(experience.getResponsibilities()) );
        experienceResponseDTO.achievements( mapAchievementTexts(experience.getAchievements()) );

        return experienceResponseDTO.build();
    }
}
