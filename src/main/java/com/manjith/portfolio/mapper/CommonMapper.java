package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.CategorySummaryResponseDTO;
import com.manjith.portfolio.dto.response.SkillSummaryResponseDTO;
import com.manjith.portfolio.entity.Category;
import com.manjith.portfolio.entity.Skill;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

/**
 * Holds mappings reused by more than one module mapper (ProjectMapper,
 * SkillMapper, and future ExperienceMapper). Kept separate so a
 * Category/Skill summary mapping is defined exactly once, not
 * copy-pasted per module.
 */
@Mapper(componentModel = "spring")
public interface CommonMapper {

    CategorySummaryResponseDTO toCategorySummaryDTO(Category category);

    SkillSummaryResponseDTO toSkillSummaryDTO(Skill skill);

    List<SkillSummaryResponseDTO> toSkillSummaryDTOList(Set<Skill> skills);
}
