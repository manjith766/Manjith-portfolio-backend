package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.SkillResponseDTO;
import com.manjith.portfolio.entity.Project;
import com.manjith.portfolio.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface SkillMapper {

    @Mapping(target = "category", source = "category")
    @Mapping(target = "projectTitles", expression = "java(mapProjectTitles(skill.getProjects()))")
    SkillResponseDTO toResponseDTO(Skill skill);

    default List<String> mapProjectTitles(Set<Project> projects) {
        if (projects == null) {
            return Collections.emptyList();
        }
        return projects.stream()
                .map(Project::getTitle)
                .collect(Collectors.toList());
    }
}
