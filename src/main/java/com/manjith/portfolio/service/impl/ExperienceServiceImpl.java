package com.manjith.portfolio.service.impl;

import com.manjith.portfolio.dto.request.ExperienceRequestDTO;
import com.manjith.portfolio.dto.response.ExperienceResponseDTO;
import com.manjith.portfolio.entity.Experience;
import com.manjith.portfolio.entity.ExperienceAchievement;
import com.manjith.portfolio.entity.ExperienceResponsibility;
import com.manjith.portfolio.entity.Skill;
import com.manjith.portfolio.exception.BusinessValidationException;
import com.manjith.portfolio.exception.ErrorCode;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.ExperienceMapper;
import com.manjith.portfolio.repository.ExperienceRepository;
import com.manjith.portfolio.repository.SkillRepository;
import com.manjith.portfolio.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * QUERY COUNT PER ENDPOINT:
 *   getAllExperience() -> 1 query for the list + at most 1 batched query
 *                          each for responsibilities, achievements, and
 *                          skills across the whole list (three separate
 *                          batches, each independent of list size, via
 *                          hibernate.default_batch_fetch_size). 4 fixed
 *                          queries total regardless of how many
 *                          experience entries exist.
 *   getExperienceById()-> 1 + 3 fixed (same three collections, single row).
 *   createExperience() -> 1 SELECT (skills batch) + 1 INSERT (cascades).
 *   updateExperience() -> 1 SELECT (existing) + 1 SELECT (skills batch) +
 *                          dirty-checked UPDATE/DELETE/INSERT on flush.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final SkillRepository skillRepository;
    private final ExperienceMapper experienceMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceResponseDTO> getAllExperience() {
        log.info("API entry: getAllExperience");

        List<Experience> experiences = experienceRepository.findAllByOrderByDisplayOrderAsc();
        experiences.forEach(e -> log.debug("Experience id={} has {} responsibilities, {} achievements, {} skills",
                e.getId(), e.getResponsibilities().size(), e.getAchievements().size(), e.getSkills().size()));

        List<ExperienceResponseDTO> response = experiences.stream()
                .map(experienceMapper::toResponseDTO)
                .collect(Collectors.toList());

        log.info("API exit: getAllExperience returning {} entries", response.size());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ExperienceResponseDTO getExperienceById(Long id) {
        log.info("API entry: getExperienceById id={}", id);

        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Experience not found id={}", id);
                    return new ResourceNotFoundException(ErrorCode.EXPERIENCE_NOT_FOUND, "No experience found with id: " + id);
                });

        log.debug("Experience id={} has {} responsibilities, {} achievements, {} skills",
                id, experience.getResponsibilities().size(), experience.getAchievements().size(), experience.getSkills().size());

        ExperienceResponseDTO response = experienceMapper.toResponseDTO(experience);
        log.info("API exit: getExperienceById returning id={}", id);
        return response;
    }

    @Override
    @Transactional
    public ExperienceResponseDTO createExperience(ExperienceRequestDTO requestDTO) {
        log.info("API entry: createExperience company={}", requestDTO.getCompanyName());

        validateDates(requestDTO);
        Set<Skill> skills = resolveSkills(requestDTO.getSkillIds());

        Experience experience = Experience.builder()
                .companyName(requestDTO.getCompanyName())
                .role(requestDTO.getRole())
                .location(requestDTO.getLocation())
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .isCurrent(requestDTO.getIsCurrent())
                .description(requestDTO.getDescription())
                .displayOrder(requestDTO.getDisplayOrder())
                .build();

        attachResponsibilities(experience, requestDTO.getResponsibilities());
        attachAchievements(experience, requestDTO.getAchievements());
        experience.replaceSkills(skills);

        Experience saved = experienceRepository.save(experience);
        log.debug("Persisted experience id={}", saved.getId());

        log.info("API exit: createExperience created id={}", saved.getId());
        return experienceMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ExperienceResponseDTO updateExperience(Long id, ExperienceRequestDTO requestDTO) {
        log.info("API entry: updateExperience id={}", id);

        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Experience not found for update, id={}", id);
                    return new ResourceNotFoundException(ErrorCode.EXPERIENCE_NOT_FOUND, "No experience found with id: " + id);
                });

        validateDates(requestDTO);
        Set<Skill> skills = resolveSkills(requestDTO.getSkillIds());

        experience.setCompanyName(requestDTO.getCompanyName());
        experience.setRole(requestDTO.getRole());
        experience.setLocation(requestDTO.getLocation());
        experience.setStartDate(requestDTO.getStartDate());
        experience.setEndDate(requestDTO.getEndDate());
        experience.setIsCurrent(requestDTO.getIsCurrent());
        experience.setDescription(requestDTO.getDescription());
        experience.setDisplayOrder(requestDTO.getDisplayOrder());

        experience.clearResponsibilities();
        attachResponsibilities(experience, requestDTO.getResponsibilities());

        experience.clearAchievements();
        attachAchievements(experience, requestDTO.getAchievements());

        experience.replaceSkills(skills);

        Experience saved = experienceRepository.save(experience);
        log.debug("Updated experience id={}", saved.getId());

        log.info("API exit: updateExperience updated id={}", saved.getId());
        return experienceMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteExperience(Long id) {
        log.info("API entry: deleteExperience id={}", id);

        if (!experienceRepository.existsById(id)) {
            log.warn("Experience not found for delete, id={}", id);
            throw new ResourceNotFoundException(ErrorCode.EXPERIENCE_NOT_FOUND, "No experience found with id: " + id);
        }

        experienceRepository.deleteById(id);
        log.info("API exit: deleteExperience deleted id={}", id);
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    /**
     * Enforces the three-way relationship between isCurrent/endDate/startDate
     * that a single-field Jakarta annotation can't express, on top of what
     * the DB CHECK constraint (chk_experience_dates) already guarantees:
     *   - isCurrent=true  -> endDate must be null
     *   - isCurrent=false -> endDate must be present
     *   - endDate (if present) must not be before startDate
     */
    private void validateDates(ExperienceRequestDTO dto) {
        boolean current = Boolean.TRUE.equals(dto.getIsCurrent());

        if (current && dto.getEndDate() != null) {
            log.warn("Validation failed: isCurrent=true but endDate was provided");
            throw new BusinessValidationException(ErrorCode.CURRENT_ROLE_WITH_END_DATE,
                    ErrorCode.CURRENT_ROLE_WITH_END_DATE.getDefaultMessage());
        }

        if (!current && dto.getEndDate() == null) {
            log.warn("Validation failed: isCurrent=false but endDate was missing");
            throw new BusinessValidationException(ErrorCode.PAST_ROLE_MISSING_END_DATE,
                    ErrorCode.PAST_ROLE_MISSING_END_DATE.getDefaultMessage());
        }

        if (dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getStartDate())) {
            log.warn("Validation failed: endDate {} is before startDate {}", dto.getEndDate(), dto.getStartDate());
            throw new BusinessValidationException(ErrorCode.INVALID_DATE_RANGE, ErrorCode.INVALID_DATE_RANGE.getDefaultMessage());
        }
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

    private void attachResponsibilities(Experience experience, List<String> responsibilities) {
        if (responsibilities == null) return;
        for (int i = 0; i < responsibilities.size(); i++) {
            experience.addResponsibility(ExperienceResponsibility.builder()
                    .text(responsibilities.get(i))
                    .displayOrder(i)
                    .build());
        }
    }

    private void attachAchievements(Experience experience, List<String> achievements) {
        if (achievements == null) return;
        for (int i = 0; i < achievements.size(); i++) {
            experience.addAchievement(ExperienceAchievement.builder()
                    .text(achievements.get(i))
                    .displayOrder(i)
                    .build());
        }
    }
}
