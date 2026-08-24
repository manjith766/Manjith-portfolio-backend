package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.SkillResponseDTO;
import com.manjith.portfolio.entity.Skill;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T17:19:48+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class SkillMapperImpl implements SkillMapper {

    @Autowired
    private CommonMapper commonMapper;

    @Override
    public SkillResponseDTO toResponseDTO(Skill skill) {
        if ( skill == null ) {
            return null;
        }

        SkillResponseDTO.SkillResponseDTOBuilder skillResponseDTO = SkillResponseDTO.builder();

        skillResponseDTO.category( commonMapper.toCategorySummaryDTO( skill.getCategory() ) );
        skillResponseDTO.id( skill.getId() );
        skillResponseDTO.name( skill.getName() );
        skillResponseDTO.proficiencyPct( skill.getProficiencyPct() );
        skillResponseDTO.yearsExperience( skill.getYearsExperience() );
        skillResponseDTO.iconUrl( skill.getIconUrl() );
        skillResponseDTO.displayOrder( skill.getDisplayOrder() );
        skillResponseDTO.createdAt( skill.getCreatedAt() );
        skillResponseDTO.updatedAt( skill.getUpdatedAt() );

        skillResponseDTO.projectTitles( mapProjectTitles(skill.getProjects()) );

        return skillResponseDTO.build();
    }
}
