package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.SeoRequestDTO;
import com.manjith.portfolio.dto.response.SeoResponseDTO;

import java.util.List;

public interface SeoMetadataService {

    SeoResponseDTO getSeoForPage(String pagePath);

    List<SeoResponseDTO> getAllSeoEntries();

    SeoResponseDTO upsertSeoEntry(SeoRequestDTO requestDTO);

    void deleteSeoEntry(Long id);
}
