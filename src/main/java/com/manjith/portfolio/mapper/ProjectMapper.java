package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.ProjectImageResponseDTO;
import com.manjith.portfolio.dto.response.ProjectResponseDTO;
import com.manjith.portfolio.dto.response.ProjectSummaryResponseDTO;
import com.manjith.portfolio.entity.Project;
import com.manjith.portfolio.entity.ProjectFeature;
import com.manjith.portfolio.entity.ProjectImage;
import com.manjith.portfolio.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Pure entity <-> DTO mapping. Does NOT touch the database and does NOT
 * contain business logic (no slug generation, no lookups) — those live in
 * ProjectServiceImpl per the mapper-discipline rule. Category/Skill
 * summary mapping is delegated to CommonMapper so it's defined once, not
 * duplicated per module.
 */
@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface ProjectMapper {

    @Mapping(target = "category", source = "category")
    @Mapping(target = "images", source = "images")
    @Mapping(target = "features", expression = "java(mapFeatureTexts(project.getFeatures()))")
    @Mapping(target = "skills", source = "skills")
    ProjectResponseDTO toResponseDTO(Project project);

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "skillNames", expression = "java(mapSkillNames(project.getSkills()))")
    ProjectSummaryResponseDTO toSummaryDTO(Project project);

    ProjectImageResponseDTO toImageResponseDTO(ProjectImage image);

    List<ProjectImageResponseDTO> toImageResponseDTOList(List<ProjectImage> images);

    default List<String> mapFeatureTexts(List<ProjectFeature> features) {
        if (features == null) {
            return Collections.emptyList();
        }
        return features.stream()
                .map(ProjectFeature::getFeatureText)
                .collect(Collectors.toList());
    }

    default List<String> mapSkillNames(Set<Skill> skills) {
        if (skills == null) {
            return Collections.emptyList();
        }
        return skills.stream()
                .map(Skill::getName)
                .collect(Collectors.toList());
    }
}
