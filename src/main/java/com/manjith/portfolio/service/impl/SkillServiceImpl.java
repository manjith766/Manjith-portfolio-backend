package com.manjith.portfolio.service.impl;

import com.manjith.portfolio.dto.request.SkillRequestDTO;
import com.manjith.portfolio.dto.response.SkillResponseDTO;
import com.manjith.portfolio.entity.Category;
import com.manjith.portfolio.entity.CategoryType;
import com.manjith.portfolio.entity.Skill;
import com.manjith.portfolio.exception.BusinessValidationException;
import com.manjith.portfolio.exception.DuplicateResourceException;
import com.manjith.portfolio.exception.ErrorCode;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.SkillMapper;
import com.manjith.portfolio.repository.CategoryRepository;
import com.manjith.portfolio.repository.SkillRepository;
import com.manjith.portfolio.service.SkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * QUERY COUNT PER ENDPOINT:
 *   getSkills(...)   -> 1 query (@EntityGraph(category)) + at most 1
 *                        batched query for projects across the whole list
 *                        (hibernate.default_batch_fetch_size), regardless
 *                        of list size. Skills are a small, bounded
 *                        collection (dozens, not thousands) so no
 *                        pagination — unlike Projects, which can grow
 *                        unbounded over a career.
 *   getSkillById(id) -> 1 query (@EntityGraph(category)) + 1 for the
 *                        projects collection = 2 fixed queries.
 *   createSkill(...) -> 1 SELECT (name uniqueness) + 1 SELECT (category)
 *                        + 1 INSERT.
 *   updateSkill(...) -> 1 SELECT (existing) + 1 SELECT (name uniqueness,
 *                        only if name changed) + 1 SELECT (category, if
 *                        changed) + 1 UPDATE.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final CategoryRepository categoryRepository;
    private final SkillMapper skillMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponseDTO> getSkills(String categorySlug) {
        log.info("API entry: getSkills categorySlug={}", categorySlug);

        List<Skill> skills = StringUtils.hasText(categorySlug)
                ? skillRepository.findAllByCategory_SlugOrderByDisplayOrderAsc(categorySlug)
                : skillRepository.findAllByOrderByDisplayOrderAsc();

        // Triggers the batched projects-collection query described above.
        skills.forEach(s -> log.debug("Skill id={} has {} linked project(s)", s.getId(), s.getProjects().size()));

        List<SkillResponseDTO> response = skills.stream().map(skillMapper::toResponseDTO).collect(Collectors.toList());
        log.info("API exit: getSkills returning {} skills", response.size());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public SkillResponseDTO getSkillById(Long id) {
        log.info("API entry: getSkillById id={}", id);

        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Skill not found id={}", id);
                    return new ResourceNotFoundException(ErrorCode.SKILL_NOT_FOUND, "No skill found with id: " + id);
                });

        int projectCount = skill.getProjects().size();
        log.debug("Skill id={} has {} linked project(s)", id, projectCount);

        SkillResponseDTO response = skillMapper.toResponseDTO(skill);
        log.info("API exit: getSkillById returning skill id={}", id);
        return response;
    }

    @Override
    @Transactional
    public SkillResponseDTO createSkill(SkillRequestDTO requestDTO) {
        log.info("API entry: createSkill name={}", requestDTO.getName());

        if (skillRepository.existsByName(requestDTO.getName())) {
            log.warn("Duplicate skill name on create: {}", requestDTO.getName());
            throw new DuplicateResourceException(ErrorCode.DUPLICATE_SKILL_NAME,
                    "A skill named '" + requestDTO.getName() + "' already exists");
        }

        Category category = resolveCategory(requestDTO.getCategoryId());

        Skill skill = Skill.builder()
                .name(requestDTO.getName())
                .category(category)
                .proficiencyPct(requestDTO.getProficiencyPct())
                .yearsExperience(requestDTO.getYearsExperience())
                .iconUrl(requestDTO.getIconUrl())
                .displayOrder(requestDTO.getDisplayOrder())
                .build();

        Skill saved = skillRepository.save(skill);
        log.debug("Persisted skill id={}", saved.getId());

        log.info("API exit: createSkill created skill id={}", saved.getId());
        return skillMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public SkillResponseDTO updateSkill(Long id, SkillRequestDTO requestDTO) {
        log.info("API entry: updateSkill id={}", id);

        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Skill not found for update, id={}", id);
                    return new ResourceNotFoundException(ErrorCode.SKILL_NOT_FOUND, "No skill found with id: " + id);
                });

        if (!requestDTO.getName().equals(skill.getName())
                && skillRepository.existsByNameAndIdNot(requestDTO.getName(), id)) {
            log.warn("Duplicate skill name on update: {} (skill id={})", requestDTO.getName(), id);
            throw new DuplicateResourceException(ErrorCode.DUPLICATE_SKILL_NAME,
                    "A different skill already uses the name '" + requestDTO.getName() + "'");
        }

        Category category = resolveCategory(requestDTO.getCategoryId());

        skill.setName(requestDTO.getName());
        skill.setCategory(category);
        skill.setProficiencyPct(requestDTO.getProficiencyPct());
        skill.setYearsExperience(requestDTO.getYearsExperience());
        skill.setIconUrl(requestDTO.getIconUrl());
        skill.setDisplayOrder(requestDTO.getDisplayOrder());

        Skill saved = skillRepository.save(skill);
        log.debug("Updated skill id={}", saved.getId());

        log.info("API exit: updateSkill updated skill id={}", saved.getId());
        return skillMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteSkill(Long id) {
        log.info("API entry: deleteSkill id={}", id);

        if (!skillRepository.existsById(id)) {
            log.warn("Skill not found for delete, id={}", id);
            throw new ResourceNotFoundException(ErrorCode.SKILL_NOT_FOUND, "No skill found with id: " + id);
        }

        // ON DELETE CASCADE on project_skills.skill_id and
        // experience_skills.skill_id (see V1__initial_schema.sql) removes
        // the join rows automatically — no manual cleanup needed here.
        skillRepository.deleteById(id);
        log.info("API exit: deleteSkill deleted skill id={}", id);
    }

    private Category resolveCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category not found id={}", categoryId);
                    return new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND,
                            "No category found with id: " + categoryId);
                });
        if (category.getType() != CategoryType.SKILL) {
            log.warn("Category id={} has type={}, expected SKILL", categoryId, category.getType());
            throw new BusinessValidationException(ErrorCode.CATEGORY_INVALID_TYPE,
                    "Category id " + categoryId + " is of type " + category.getType() + ", expected SKILL");
        }
        return category;
    }
}
