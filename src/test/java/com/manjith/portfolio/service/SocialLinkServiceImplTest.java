package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.SocialLinkRequestDTO;
import com.manjith.portfolio.exception.DuplicateResourceException;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.SocialLinkMapper;
import com.manjith.portfolio.repository.SocialLinkRepository;
import com.manjith.portfolio.service.impl.SocialLinkServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SocialLinkServiceImplTest {

    @Mock
    private SocialLinkRepository socialLinkRepository;
    @Mock
    private SocialLinkMapper socialLinkMapper;

    @InjectMocks
    private SocialLinkServiceImpl socialLinkService;

    @Test
    void createSocialLink_throwsDuplicateResourceException_whenPlatformAlreadyExists() {
        SocialLinkRequestDTO request = SocialLinkRequestDTO.builder()
                .platform("GITHUB").url("https://github.com/manjith766").displayOrder(1).build();

        when(socialLinkRepository.existsByPlatform("GITHUB")).thenReturn(true);

        assertThatThrownBy(() -> socialLinkService.createSocialLink(request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(socialLinkRepository, never()).save(any());
    }

    @Test
    void deleteSocialLink_throwsResourceNotFoundException_whenNotExists() {
        when(socialLinkRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> socialLinkService.deleteSocialLink(42L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(socialLinkRepository, never()).deleteById(any());
    }
}
