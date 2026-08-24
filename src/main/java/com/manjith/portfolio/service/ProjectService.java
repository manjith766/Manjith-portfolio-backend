package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.ProjectRequestDTO;
import com.manjith.portfolio.dto.response.PageResponseDTO;
import com.manjith.portfolio.dto.response.ProjectResponseDTO;
import com.manjith.portfolio.dto.response.ProjectSummaryResponseDTO;

public interface ProjectService {

    PageResponseDTO<ProjectSummaryResponseDTO> getProjects(
            String categorySlug, Boolean featured, String search,
            int page, int size, String sortBy, String sortDirection);

    ProjectResponseDTO getProjectBySlug(String slug);

    ProjectResponseDTO createProject(ProjectRequestDTO requestDTO);

    ProjectResponseDTO updateProject(Long id, ProjectRequestDTO requestDTO);

    void deleteProject(Long id);

    void incrementViewCount(String slug);
}
