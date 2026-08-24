package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.SkillRequestDTO;
import com.manjith.portfolio.dto.response.SkillResponseDTO;

import java.util.List;

public interface SkillService {

    List<SkillResponseDTO> getSkills(String categorySlug);

    SkillResponseDTO getSkillById(Long id);

    SkillResponseDTO createSkill(SkillRequestDTO requestDTO);

    SkillResponseDTO updateSkill(Long id, SkillRequestDTO requestDTO);

    void deleteSkill(Long id);
}
