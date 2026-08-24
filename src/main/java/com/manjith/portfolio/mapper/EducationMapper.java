package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.EducationResponseDTO;
import com.manjith.portfolio.entity.Education;
import com.manjith.portfolio.entity.EducationAchievement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    @Mapping(target = "achievements", expression = "java(mapAchievementTexts(education.getAchievements()))")
    EducationResponseDTO toResponseDTO(Education education);

    default List<String> mapAchievementTexts(List<EducationAchievement> achievements) {
        if (achievements == null) {
            return Collections.emptyList();
        }
        return achievements.stream()
                .map(EducationAchievement::getText)
                .collect(Collectors.toList());
    }
}
