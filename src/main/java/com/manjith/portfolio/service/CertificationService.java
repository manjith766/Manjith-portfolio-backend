package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.CertificationRequestDTO;
import com.manjith.portfolio.dto.response.CertificationResponseDTO;

import java.util.List;

public interface CertificationService {

    List<CertificationResponseDTO> getAllCertifications();

    CertificationResponseDTO getCertificationById(Long id);

    CertificationResponseDTO createCertification(CertificationRequestDTO requestDTO);

    CertificationResponseDTO updateCertification(Long id, CertificationRequestDTO requestDTO);

    void deleteCertification(Long id);
}
