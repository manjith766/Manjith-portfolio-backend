package com.manjith.portfolio.service.impl;

import com.manjith.portfolio.dto.request.SeoRequestDTO;
import com.manjith.portfolio.dto.response.SeoResponseDTO;
import com.manjith.portfolio.entity.SeoMetadata;
import com.manjith.portfolio.exception.ErrorCode;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.SeoMetadataMapper;
import com.manjith.portfolio.repository.SeoMetadataRepository;
import com.manjith.portfolio.service.SeoMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * upsertSeoEntry is keyed by pagePath (from the request body, not a path
 * variable) rather than id — an admin editing a page's SEO thinks in
 * terms of "the /about page", not "SEO row 7". find-or-create by
 * pagePath keeps that the natural unit of upsert.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeoMetadataServiceImpl implements SeoMetadataService {

    private final SeoMetadataRepository seoMetadataRepository;
    private final SeoMetadataMapper seoMetadataMapper;

    @Override
    @Transactional(readOnly = true)
    public SeoResponseDTO getSeoForPage(String pagePath) {
        log.info("API entry: getSeoForPage pagePath={}", pagePath);

        SeoMetadata seoMetadata = seoMetadataRepository.findByPagePath(pagePath)
                .orElseThrow(() -> {
                    log.warn("SEO entry not found for pagePath={}", pagePath);
                    return new ResourceNotFoundException(ErrorCode.SEO_ENTRY_NOT_FOUND, "No SEO entry found for pagePath: " + pagePath);
                });

        log.info("API exit: getSeoForPage returning pagePath={}", pagePath);
        return seoMetadataMapper.toResponseDTO(seoMetadata);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeoResponseDTO> getAllSeoEntries() {
        log.info("API entry: getAllSeoEntries");
        List<SeoResponseDTO> response = seoMetadataRepository.findAll().stream()
                .map(seoMetadataMapper::toResponseDTO)
                .collect(Collectors.toList());
        log.info("API exit: getAllSeoEntries returning {} entries", response.size());
        return response;
    }

    @Override
    @Transactional
    public SeoResponseDTO upsertSeoEntry(SeoRequestDTO requestDTO) {
        log.info("API entry: upsertSeoEntry pagePath={}", requestDTO.getPagePath());

        SeoMetadata seoMetadata = seoMetadataRepository.findByPagePath(requestDTO.getPagePath())
                .orElseGet(() -> SeoMetadata.builder().pagePath(requestDTO.getPagePath()).build());

        seoMetadata.setMetaTitle(requestDTO.getMetaTitle());
        seoMetadata.setMetaDescription(requestDTO.getMetaDescription());
        seoMetadata.setOgImageUrl(requestDTO.getOgImageUrl());
        seoMetadata.setCanonicalUrl(requestDTO.getCanonicalUrl());

        SeoMetadata saved = seoMetadataRepository.save(seoMetadata);
        log.debug("Upserted SEO entry pagePath={}", saved.getPagePath());

        log.info("API exit: upsertSeoEntry saved pagePath={}", saved.getPagePath());
        return seoMetadataMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteSeoEntry(Long id) {
        log.info("API entry: deleteSeoEntry id={}", id);

        if (!seoMetadataRepository.existsById(id)) {
            log.warn("SEO entry not found for delete, id={}", id);
            throw new ResourceNotFoundException(ErrorCode.SEO_ENTRY_NOT_FOUND, "No SEO entry found with id: " + id);
        }

        seoMetadataRepository.deleteById(id);
        log.info("API exit: deleteSeoEntry deleted id={}", id);
    }
}
