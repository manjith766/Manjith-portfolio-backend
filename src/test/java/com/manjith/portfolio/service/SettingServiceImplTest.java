package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.SettingRequestDTO;
import com.manjith.portfolio.exception.BusinessValidationException;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.SettingMapper;
import com.manjith.portfolio.repository.SettingRepository;
import com.manjith.portfolio.service.impl.SettingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingServiceImplTest {

    @Mock
    private SettingRepository settingRepository;
    @Mock
    private SettingMapper settingMapper;

    @InjectMocks
    private SettingServiceImpl settingService;

    @Test
    void upsertSetting_throwsBusinessValidationException_whenKeyHasInvalidCharacters() {
        SettingRequestDTO request = SettingRequestDTO.builder().value("some value").build();

        assertThatThrownBy(() -> settingService.upsertSetting("bad key with spaces", request))
                .isInstanceOf(BusinessValidationException.class);
    }

    @Test
    void deleteSetting_throwsResourceNotFoundException_whenKeyNotFound() {
        when(settingRepository.findBySettingKey("missing.key")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> settingService.deleteSetting("missing.key"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
