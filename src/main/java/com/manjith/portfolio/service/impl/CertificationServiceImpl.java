package com.manjith.portfolio.service.impl;

import com.manjith.portfolio.dto.request.CertificationRequestDTO;
import com.manjith.portfolio.dto.response.CertificationResponseDTO;
import com.manjith.portfolio.entity.Certification;
import com.manjith.portfolio.exception.ErrorCode;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.CertificationMapper;
import com.manjith.portfolio.repository.CertificationRepository;
import com.manjith.portfolio.service.CertificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * QUERY COUNT PER ENDPOINT: this is the simplest module in the codebase —
 * no child collections, no ManyToOne/ManyToMany relations to fetch-join or
 * batch. Every operation is exactly 1 query (2 for update: fetch + flush).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;
    private final CertificationMapper certificationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CertificationResponseDTO> getAllCertifications() {
        log.info("API entry: getAllCertifications");

        List<Certification> certifications = certificationRepository.findAllByOrderByDisplayOrderAsc();
        List<CertificationResponseDTO> response = certifications.stream()
                .map(certificationMapper::toResponseDTO)
                .collect(Collectors.toList());

        log.info("API exit: getAllCertifications returning {} entries", response.size());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public CertificationResponseDTO getCertificationById(Long id) {
        log.info("API entry: getCertificationById id={}", id);

        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Certification not found id={}", id);
                    return new ResourceNotFoundException(ErrorCode.CERTIFICATION_NOT_FOUND,
                            "No certification found with id: " + id);
                });

        CertificationResponseDTO response = certificationMapper.toResponseDTO(certification);
        log.info("API exit: getCertificationById returning id={}", id);
        return response;
    }

    @Override
    @Transactional
    public CertificationResponseDTO createCertification(CertificationRequestDTO requestDTO) {
        log.info("API entry: createCertification title={}", requestDTO.getTitle());

        Certification certification = Certification.builder()
                .title(requestDTO.getTitle())
                .issuer(requestDTO.getIssuer())
                .issueDate(requestDTO.getIssueDate())
                .credentialUrl(requestDTO.getCredentialUrl())
                .imageUrl(requestDTO.getImageUrl())
                .displayOrder(requestDTO.getDisplayOrder())
                .build();

        Certification saved = certificationRepository.save(certification);
        log.debug("Persisted certification id={}", saved.getId());

        log.info("API exit: createCertification created id={}", saved.getId());
        return certificationMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public CertificationResponseDTO updateCertification(Long id, CertificationRequestDTO requestDTO) {
        log.info("API entry: updateCertification id={}", id);

        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Certification not found for update, id={}", id);
                    return new ResourceNotFoundException(ErrorCode.CERTIFICATION_NOT_FOUND,
                            "No certification found with id: " + id);
                });

        certification.setTitle(requestDTO.getTitle());
        certification.setIssuer(requestDTO.getIssuer());
        certification.setIssueDate(requestDTO.getIssueDate());
        certification.setCredentialUrl(requestDTO.getCredentialUrl());
        certification.setImageUrl(requestDTO.getImageUrl());
        certification.setDisplayOrder(requestDTO.getDisplayOrder());

        Certification saved = certificationRepository.save(certification);
        log.debug("Updated certification id={}", saved.getId());

        log.info("API exit: updateCertification updated id={}", saved.getId());
        return certificationMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteCertification(Long id) {
        log.info("API entry: deleteCertification id={}", id);

        if (!certificationRepository.existsById(id)) {
            log.warn("Certification not found for delete, id={}", id);
            throw new ResourceNotFoundException(ErrorCode.CERTIFICATION_NOT_FOUND, "No certification found with id: " + id);
        }

        certificationRepository.deleteById(id);
        log.info("API exit: deleteCertification deleted id={}", id);
    }
}
