package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.CategorySummaryResponseDTO;
import com.manjith.portfolio.dto.response.SkillSummaryResponseDTO;
import com.manjith.portfolio.entity.Category;
import com.manjith.portfolio.entity.Skill;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T17:19:48+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class CommonMapperImpl implements CommonMapper {

    @Override
    public CategorySummaryResponseDTO toCategorySummaryDTO(Category category) {
        if ( category == null ) {
            return null;
        }

        CategorySummaryResponseDTO.CategorySummaryResponseDTOBuilder categorySummaryResponseDTO = CategorySummaryResponseDTO.builder();

        categorySummaryResponseDTO.id( category.getId() );
        categorySummaryResponseDTO.name( category.getName() );
        categorySummaryResponseDTO.slug( category.getSlug() );

        return categorySummaryResponseDTO.build();
    }

    @Override
    public SkillSummaryResponseDTO toSkillSummaryDTO(Skill skill) {
        if ( skill == null ) {
            return null;
        }

        SkillSummaryResponseDTO.SkillSummaryResponseDTOBuilder skillSummaryResponseDTO = SkillSummaryResponseDTO.builder();

        skillSummaryResponseDTO.id( skill.getId() );
        skillSummaryResponseDTO.name( skill.getName() );
        skillSummaryResponseDTO.iconUrl( skill.getIconUrl() );

        return skillSummaryResponseDTO.build();
    }

    @Override
    public List<SkillSummaryResponseDTO> toSkillSummaryDTOList(Set<Skill> skills) {
        if ( skills == null ) {
            return null;
        }

        List<SkillSummaryResponseDTO> list = new ArrayList<SkillSummaryResponseDTO>( skills.size() );
        for ( Skill skill : skills ) {
            list.add( toSkillSummaryDTO( skill ) );
        }

        return list;
    }
}
