package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.SocialLinkRequestDTO;
import com.manjith.portfolio.dto.response.SocialLinkResponseDTO;

import java.util.List;

public interface SocialLinkService {

    List<SocialLinkResponseDTO> getAllSocialLinks();

    SocialLinkResponseDTO createSocialLink(SocialLinkRequestDTO requestDTO);

    SocialLinkResponseDTO updateSocialLink(Long id, SocialLinkRequestDTO requestDTO);

    void deleteSocialLink(Long id);
}
