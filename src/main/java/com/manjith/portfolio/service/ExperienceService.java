package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.ExperienceRequestDTO;
import com.manjith.portfolio.dto.response.ExperienceResponseDTO;

import java.util.List;

public interface ExperienceService {

    List<ExperienceResponseDTO> getAllExperience();

    ExperienceResponseDTO getExperienceById(Long id);

    ExperienceResponseDTO createExperience(ExperienceRequestDTO requestDTO);

    ExperienceResponseDTO updateExperience(Long id, ExperienceRequestDTO requestDTO);

    void deleteExperience(Long id);
}
