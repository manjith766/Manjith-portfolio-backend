package com.manjith.portfolio.service.impl;

import com.manjith.portfolio.dto.request.SocialLinkRequestDTO;
import com.manjith.portfolio.dto.response.SocialLinkResponseDTO;
import com.manjith.portfolio.entity.SocialLink;
import com.manjith.portfolio.exception.DuplicateResourceException;
import com.manjith.portfolio.exception.ErrorCode;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.SocialLinkMapper;
import com.manjith.portfolio.repository.SocialLinkRepository;
import com.manjith.portfolio.service.SocialLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLinkServiceImpl implements SocialLinkService {

    private final SocialLinkRepository socialLinkRepository;
    private final SocialLinkMapper socialLinkMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SocialLinkResponseDTO> getAllSocialLinks() {
        log.info("API entry: getAllSocialLinks");
        List<SocialLinkResponseDTO> response = socialLinkRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(socialLinkMapper::toResponseDTO)
                .collect(Collectors.toList());
        log.info("API exit: getAllSocialLinks returning {} links", response.size());
        return response;
    }

    @Override
    @Transactional
    public SocialLinkResponseDTO createSocialLink(SocialLinkRequestDTO requestDTO) {
        log.info("API entry: createSocialLink platform={}", requestDTO.getPlatform());

        if (socialLinkRepository.existsByPlatform(requestDTO.getPlatform())) {
            log.warn("Duplicate platform on create: {}", requestDTO.getPlatform());
            throw new DuplicateResourceException(ErrorCode.DUPLICATE_SOCIAL_LINK_PLATFORM,
                    "A social link for platform '" + requestDTO.getPlatform() + "' already exists");
        }

        SocialLink socialLink = SocialLink.builder()
                .platform(requestDTO.getPlatform())
                .url(requestDTO.getUrl())
                .icon(requestDTO.getIcon())
                .displayOrder(requestDTO.getDisplayOrder())
                .build();

        SocialLink saved = socialLinkRepository.save(socialLink);
        log.debug("Persisted social link id={}", saved.getId());

        log.info("API exit: createSocialLink created id={}", saved.getId());
        return socialLinkMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public SocialLinkResponseDTO updateSocialLink(Long id, SocialLinkRequestDTO requestDTO) {
        log.info("API entry: updateSocialLink id={}", id);

        SocialLink socialLink = socialLinkRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Social link not found for update, id={}", id);
                    return new ResourceNotFoundException(ErrorCode.SOCIAL_LINK_NOT_FOUND, "No social link found with id: " + id);
                });

        if (!requestDTO.getPlatform().equals(socialLink.getPlatform())
                && socialLinkRepository.existsByPlatformAndIdNot(requestDTO.getPlatform(), id)) {
            log.warn("Duplicate platform on update: {} (id={})", requestDTO.getPlatform(), id);
            throw new DuplicateResourceException(ErrorCode.DUPLICATE_SOCIAL_LINK_PLATFORM,
                    "A different social link already uses platform '" + requestDTO.getPlatform() + "'");
        }

        socialLink.setPlatform(requestDTO.getPlatform());
        socialLink.setUrl(requestDTO.getUrl());
        socialLink.setIcon(requestDTO.getIcon());
        socialLink.setDisplayOrder(requestDTO.getDisplayOrder());

        SocialLink saved = socialLinkRepository.save(socialLink);
        log.debug("Updated social link id={}", saved.getId());

        log.info("API exit: updateSocialLink updated id={}", saved.getId());
        return socialLinkMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteSocialLink(Long id) {
        log.info("API entry: deleteSocialLink id={}", id);

        if (!socialLinkRepository.existsById(id)) {
            log.warn("Social link not found for delete, id={}", id);
            throw new ResourceNotFoundException(ErrorCode.SOCIAL_LINK_NOT_FOUND, "No social link found with id: " + id);
        }

        socialLinkRepository.deleteById(id);
        log.info("API exit: deleteSocialLink deleted id={}", id);
    }
}
