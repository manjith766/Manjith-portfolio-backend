package com.manjith.portfolio.service.impl;

import com.manjith.portfolio.constants.AppConstants;
import com.manjith.portfolio.dto.request.ProjectImageRequestDTO;
import com.manjith.portfolio.dto.request.ProjectRequestDTO;
import com.manjith.portfolio.dto.response.PageResponseDTO;
import com.manjith.portfolio.dto.response.ProjectResponseDTO;
import com.manjith.portfolio.dto.response.ProjectSummaryResponseDTO;
import com.manjith.portfolio.entity.Category;
import com.manjith.portfolio.entity.CategoryType;
import com.manjith.portfolio.entity.Project;
import com.manjith.portfolio.entity.ProjectFeature;
import com.manjith.portfolio.entity.ProjectImage;
import com.manjith.portfolio.entity.Skill;
import com.manjith.portfolio.exception.BusinessValidationException;
import com.manjith.portfolio.exception.DuplicateResourceException;
import com.manjith.portfolio.exception.ErrorCode;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.ProjectMapper;
import com.manjith.portfolio.repository.CategoryRepository;
import com.manjith.portfolio.repository.ProjectRepository;
import com.manjith.portfolio.repository.SkillRepository;
import com.manjith.portfolio.repository.specification.ProjectSpecification;
import com.manjith.portfolio.service.ProjectService;
import com.manjith.portfolio.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * QUERY COUNT PER ENDPOINT (see also Javadoc on ProjectRepository):
 *
 *   getProjects(...)        -> 1 query (page of projects + category, via
 *                               @EntityGraph) + at most 1 batched query for
 *                               skills across the whole page (Hibernate
 *                               default_batch_fetch_size), regardless of
 *                               page size. Total: 2 queries, not 1 + N.
 *
 *   getProjectBySlug(slug)  -> 1 query (project + category, via
 *                               @EntityGraph) + 1 for images + 1 for
 *                               features + 1 for skills = 4 fixed queries
 *                               for ONE project. Not chosen as a single
 *                               query with 3 JOIN FETCHes on 3 collections
 *                               because that produces a Cartesian product
 *                               (rows = images x features x skills) and
 *                               Hibernate additionally throws
 *                               MultipleBagFetchException for >1 List-typed
 *                               collection fetched in one JPQL query.
 *
 *   createProject(...)      -> 1 SELECT (slug uniqueness check) + 1 SELECT
 *                               (category, if provided) + 1 SELECT (skills
 *                               batch by id) + 1 INSERT (project, cascades
 *                               to images/features/skill-links). No loops
 *                               over individual DB calls.
 *
 *   updateProject(...)      -> 1 SELECT (existing project + category) + 1
 *                               SELECT (slug collision check, only if title
 *                               changed) + 1 SELECT (category, if changed)
 *                               + 1 SELECT (skills batch) + Hibernate dirty
 *                               checking issues UPDATE/DELETE/INSERT for
 *                               changed associations on flush.
 *
 *   incrementViewCount(...) -> 1 atomic UPDATE, no SELECT needed first.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ProjectSummaryResponseDTO> getProjects(
            String categorySlug, Boolean featured, String search,
            int page, int size, String sortBy, String sortDirection) {

        log.info("API entry: getProjects categorySlug={}, featured={}, search={}, page={}, size={}",
                categorySlug, featured, search, page, size);

        int safePage = Math.max(page, AppConstants.DEFAULT_PAGE_NUMBER);
        int safeSize = size <= 0 ? AppConstants.DEFAULT_PAGE_SIZE : Math.min(size, AppConstants.MAX_PAGE_SIZE);
        String safeSortBy = sortBy == null || sortBy.isBlank() ? AppConstants.DEFAULT_SORT_BY : sortBy;
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(direction, safeSortBy));
        Specification<Project> spec = ProjectSpecification.withFilters(categorySlug, featured, search);

        Page<Project> projectPage = projectRepository.findAll(spec, pageable);
        log.debug("Fetched {} projects (page {} of {})", projectPage.getNumberOfElements(), safePage, projectPage.getTotalPages());

        Page<ProjectSummaryResponseDTO> dtoPage = projectPage.map(projectMapper::toSummaryDTO);

        log.info("API exit: getProjects returning {} of {} total projects",
                dtoPage.getNumberOfElements(), dtoPage.getTotalElements());
        return PageResponseDTO.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectBySlug(String slug) {
        log.info("API entry: getProjectBySlug slug={}", slug);

        Project project = projectRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    log.warn("Project not found for slug={}", slug);
                    return new ResourceNotFoundException(ErrorCode.PROJECT_NOT_FOUND,
                            "No project found with slug: " + slug);
                });

        // Triggers 3 fixed lazy-loading queries (images, features, skills) —
        // safe here because we are inside the read-only transaction.
        int imageCount = project.getImages().size();
        int featureCount = project.getFeatures().size();
        int skillCount = project.getSkills().size();
        log.debug("Loaded project id={} with {} images, {} features, {} skills",
                project.getId(), imageCount, featureCount, skillCount);

        ProjectResponseDTO response = projectMapper.toResponseDTO(project);
        log.info("API exit: getProjectBySlug returning project id={}", project.getId());
        return response;
    }

    @Override
    @Transactional
    public ProjectResponseDTO createProject(ProjectRequestDTO requestDTO) {
        log.info("API entry: createProject title={}", requestDTO.getTitle());

        String slug = SlugUtil.toSlug(requestDTO.getTitle());
        if (projectRepository.existsBySlug(slug)) {
            log.warn("Duplicate slug on create: {}", slug);
            throw new DuplicateResourceException(ErrorCode.DUPLICATE_SLUG,
                    "A project resolving to slug '" + slug + "' already exists. Use a different title.");
        }

        Category category = resolveCategory(requestDTO.getCategoryId());
        Set<Skill> skills = resolveSkills(requestDTO.getSkillIds());

        Project project = Project.builder()
                .title(requestDTO.getTitle())
                .slug(slug)
                .description(requestDTO.getDescription())
                .shortDescription(requestDTO.getShortDescription())
                .category(category)
                .githubUrl(requestDTO.getGithubUrl())
                .liveDemoUrl(requestDTO.getLiveDemoUrl())
                .coverImageUrl(requestDTO.getCoverImageUrl())
                .isFeatured(requestDTO.getIsFeatured())
                .displayOrder(requestDTO.getDisplayOrder())
                .viewCount(0L)
                .build();

        attachFeatures(project, requestDTO.getFeatures());
        attachImages(project, requestDTO.getImages());
        project.replaceSkills(skills);

        Project saved = projectRepository.save(project);
        log.debug("Persisted project id={} slug={}", saved.getId(), saved.getSlug());

        log.info("API exit: createProject created project id={}", saved.getId());
        return projectMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ProjectResponseDTO updateProject(Long id, ProjectRequestDTO requestDTO) {
        log.info("API entry: updateProject id={}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Project not found for update, id={}", id);
                    return new ResourceNotFoundException(ErrorCode.PROJECT_NOT_FOUND,
                            "No project found with id: " + id);
                });

        String newSlug = SlugUtil.toSlug(requestDTO.getTitle());
        if (!newSlug.equals(project.getSlug()) && projectRepository.existsBySlugAndIdNot(newSlug, id)) {
            log.warn("Duplicate slug on update: {} (project id={})", newSlug, id);
            throw new DuplicateResourceException(ErrorCode.DUPLICATE_SLUG,
                    "A different project already resolves to slug '" + newSlug + "'. Use a different title.");
        }

        Category category = resolveCategory(requestDTO.getCategoryId());
        Set<Skill> skills = resolveSkills(requestDTO.getSkillIds());

        project.setTitle(requestDTO.getTitle());
        project.setSlug(newSlug);
        project.setDescription(requestDTO.getDescription());
        project.setShortDescription(requestDTO.getShortDescription());
        project.setCategory(category);
        project.setGithubUrl(requestDTO.getGithubUrl());
        project.setLiveDemoUrl(requestDTO.getLiveDemoUrl());
        project.setCoverImageUrl(requestDTO.getCoverImageUrl());
        project.setIsFeatured(requestDTO.getIsFeatured());
        project.setDisplayOrder(requestDTO.getDisplayOrder());

        // Full replace of child collections — orphanRemoval=true on both
        // OneToMany mappings means clear() correctly issues DELETEs for the
        // removed rows on flush, and the new adds issue INSERTs. Atomic as
        // part of this single @Transactional method — no partial writes.
        project.clearFeatures();
        attachFeatures(project, requestDTO.getFeatures());

        project.clearImages();
        attachImages(project, requestDTO.getImages());

        project.replaceSkills(skills);

        Project saved = projectRepository.save(project);
        log.debug("Updated project id={} slug={}", saved.getId(), saved.getSlug());

        log.info("API exit: updateProject updated project id={}", saved.getId());
        return projectMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        log.info("API entry: deleteProject id={}", id);

        if (!projectRepository.existsById(id)) {
            log.warn("Project not found for delete, id={}", id);
            throw new ResourceNotFoundException(ErrorCode.PROJECT_NOT_FOUND, "No project found with id: " + id);
        }

        // DB-level ON DELETE CASCADE on project_images/project_features and
        // ON DELETE CASCADE on project_skills handles child cleanup.
        projectRepository.deleteById(id);
        log.info("API exit: deleteProject deleted project id={}", id);
    }

    @Override
    @Transactional
    public void incrementViewCount(String slug) {
        log.info("API entry: incrementViewCount slug={}", slug);

        int updatedRows = projectRepository.incrementViewCountBySlug(slug);
        if (updatedRows == 0) {
            log.warn("View-count increment failed, no project for slug={}", slug);
            throw new ResourceNotFoundException(ErrorCode.PROJECT_NOT_FOUND, "No project found with slug: " + slug);
        }

        log.info("API exit: incrementViewCount incremented slug={}", slug);
    }

    // ------------------------------------------------------------------
    // Private helpers — validation and association wiring only, no
    // controller/mapper logic lives here.
    // ------------------------------------------------------------------

    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category not found id={}", categoryId);
                    return new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND,
                            "No category found with id: " + categoryId);
                });
        if (category.getType() != CategoryType.PROJECT) {
            log.warn("Category id={} has type={}, expected PROJECT", categoryId, category.getType());
            throw new BusinessValidationException(ErrorCode.CATEGORY_INVALID_TYPE,
                    "Category id " + categoryId + " is of type " + category.getType() + ", expected PROJECT");
        }
        return category;
    }

    private Set<Skill> resolveSkills(Set<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return new LinkedHashSet<>();
        }
        List<Skill> found = skillRepository.findAllByIdIn(skillIds);
        if (found.size() != skillIds.size()) {
            Set<Long> foundIds = found.stream().map(Skill::getId).collect(Collectors.toSet());
            Set<Long> missing = skillIds.stream().filter(reqId -> !foundIds.contains(reqId)).collect(Collectors.toSet());
            log.warn("Skill ids not found: {}", missing);
            throw new ResourceNotFoundException(ErrorCode.SKILL_NOT_FOUND, "Skill ids not found: " + missing);
        }
        return new LinkedHashSet<>(found);
    }

    private void attachFeatures(Project project, List<String> features) {
        if (features == null) {
            return;
        }
        for (int i = 0; i < features.size(); i++) {
            ProjectFeature feature = ProjectFeature.builder()
                    .featureText(features.get(i))
                    .displayOrder(i)
                    .build();
            project.addFeature(feature);
        }
    }

    private void attachImages(Project project, List<ProjectImageRequestDTO> images) {
        if (images == null) {
            return;
        }
        for (ProjectImageRequestDTO imageDTO : images) {
            ProjectImage image = ProjectImage.builder()
                    .imageUrl(imageDTO.getImageUrl())
                    .altText(imageDTO.getAltText())
                    .displayOrder(imageDTO.getDisplayOrder() == null ? 0 : imageDTO.getDisplayOrder())
                    .build();
            project.addImage(image);
        }
    }
}
