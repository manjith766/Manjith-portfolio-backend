package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.ExperienceResponseDTO;
import com.manjith.portfolio.entity.Experience;
import com.manjith.portfolio.entity.ExperienceAchievement;
import com.manjith.portfolio.entity.ExperienceResponsibility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface ExperienceMapper {

    @Mapping(target = "responsibilities", expression = "java(mapResponsibilityTexts(experience.getResponsibilities()))")
    @Mapping(target = "achievements", expression = "java(mapAchievementTexts(experience.getAchievements()))")
    @Mapping(target = "skills", source = "skills")
    ExperienceResponseDTO toResponseDTO(Experience experience);

    default List<String> mapResponsibilityTexts(List<ExperienceResponsibility> responsibilities) {
        if (responsibilities == null) {
            return Collections.emptyList();
        }
        return responsibilities.stream().map(ExperienceResponsibility::getText).collect(Collectors.toList());
    }

    default List<String> mapAchievementTexts(List<ExperienceAchievement> achievements) {
        if (achievements == null) {
            return Collections.emptyList();
        }
        return achievements.stream().map(ExperienceAchievement::getText).collect(Collectors.toList());
    }
}
