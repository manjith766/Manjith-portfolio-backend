package com.manjith.portfolio.service.impl;

import com.manjith.portfolio.dto.request.EducationRequestDTO;
import com.manjith.portfolio.dto.response.EducationResponseDTO;
import com.manjith.portfolio.entity.Education;
import com.manjith.portfolio.entity.EducationAchievement;
import com.manjith.portfolio.exception.BusinessValidationException;
import com.manjith.portfolio.exception.ErrorCode;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.EducationMapper;
import com.manjith.portfolio.repository.EducationRepository;
import com.manjith.portfolio.service.EducationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * QUERY COUNT PER ENDPOINT:
 *   getAllEducation()   -> 1 query for the list + at most 1 batched query
 *                           for achievements across the whole list
 *                           (hibernate.default_batch_fetch_size),
 *                           regardless of how many entries exist.
 *   getEducationById()  -> 1 + 1 fixed (single row's achievements).
 *   createEducation()   -> 1 INSERT (cascades to achievements).
 *   updateEducation()   -> 1 SELECT (existing) + dirty-checked
 *                           UPDATE/DELETE/INSERT on flush for the
 *                           replaced achievements collection.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final EducationMapper educationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EducationResponseDTO> getAllEducation() {
        log.info("API entry: getAllEducation");

        List<Education> educationList = educationRepository.findAllByOrderByDisplayOrderAsc();
        educationList.forEach(e -> log.debug("Education id={} has {} achievements", e.getId(), e.getAchievements().size()));

        List<EducationResponseDTO> response = educationList.stream()
                .map(educationMapper::toResponseDTO)
                .collect(Collectors.toList());

        log.info("API exit: getAllEducation returning {} entries", response.size());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public EducationResponseDTO getEducationById(Long id) {
        log.info("API entry: getEducationById id={}", id);

        Education education = educationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Education not found id={}", id);
                    return new ResourceNotFoundException(ErrorCode.EDUCATION_NOT_FOUND, "No education entry found with id: " + id);
                });

        log.debug("Education id={} has {} achievements", id, education.getAchievements().size());

        EducationResponseDTO response = educationMapper.toResponseDTO(education);
        log.info("API exit: getEducationById returning id={}", id);
        return response;
    }

    @Override
    @Transactional
    public EducationResponseDTO createEducation(EducationRequestDTO requestDTO) {
        log.info("API entry: createEducation institution={}", requestDTO.getInstitution());

        validateDates(requestDTO);

        Education education = Education.builder()
                .degree(requestDTO.getDegree())
                .institution(requestDTO.getInstitution())
                .location(requestDTO.getLocation())
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .cgpa(requestDTO.getCgpa())
                .description(requestDTO.getDescription())
                .displayOrder(requestDTO.getDisplayOrder())
                .build();

        attachAchievements(education, requestDTO.getAchievements());

        Education saved = educationRepository.save(education);
        log.debug("Persisted education id={}", saved.getId());

        log.info("API exit: createEducation created id={}", saved.getId());
        return educationMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public EducationResponseDTO updateEducation(Long id, EducationRequestDTO requestDTO) {
        log.info("API entry: updateEducation id={}", id);

        Education education = educationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Education not found for update, id={}", id);
                    return new ResourceNotFoundException(ErrorCode.EDUCATION_NOT_FOUND, "No education entry found with id: " + id);
                });

        validateDates(requestDTO);

        education.setDegree(requestDTO.getDegree());
        education.setInstitution(requestDTO.getInstitution());
        education.setLocation(requestDTO.getLocation());
        education.setStartDate(requestDTO.getStartDate());
        education.setEndDate(requestDTO.getEndDate());
        education.setCgpa(requestDTO.getCgpa());
        education.setDescription(requestDTO.getDescription());
        education.setDisplayOrder(requestDTO.getDisplayOrder());

        education.clearAchievements();
        attachAchievements(education, requestDTO.getAchievements());

        Education saved = educationRepository.save(education);
        log.debug("Updated education id={}", saved.getId());

        log.info("API exit: updateEducation updated id={}", saved.getId());
        return educationMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteEducation(Long id) {
        log.info("API entry: deleteEducation id={}", id);

        if (!educationRepository.existsById(id)) {
            log.warn("Education not found for delete, id={}", id);
            throw new ResourceNotFoundException(ErrorCode.EDUCATION_NOT_FOUND, "No education entry found with id: " + id);
        }

        educationRepository.deleteById(id);
        log.info("API exit: deleteEducation deleted id={}", id);
    }

    /**
     * Mirrors chk_education_dates at the application layer so the error is
     * a clean 400 with a clear message, rather than a raw DB constraint
     * violation surfacing as a 500.
     */
    private void validateDates(EducationRequestDTO dto) {
        if (dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getStartDate())) {
            log.warn("Validation failed: endDate {} is before startDate {}", dto.getEndDate(), dto.getStartDate());
            throw new BusinessValidationException(ErrorCode.EDUCATION_INVALID_DATE_RANGE,
                    ErrorCode.EDUCATION_INVALID_DATE_RANGE.getDefaultMessage());
        }
    }

    private void attachAchievements(Education education, List<String> achievements) {
        if (achievements == null) return;
        for (int i = 0; i < achievements.size(); i++) {
            education.addAchievement(EducationAchievement.builder()
                    .text(achievements.get(i))
                    .displayOrder(i)
                    .build());
        }
    }
}
