package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.SettingRequestDTO;
import com.manjith.portfolio.dto.response.SettingResponseDTO;

import java.util.List;
import java.util.Map;

public interface SettingService {

    Map<String, String> getPublicSettingsMap();

    List<SettingResponseDTO> getAllSettingsForAdmin();

    SettingResponseDTO upsertSetting(String key, SettingRequestDTO requestDTO);

    void deleteSetting(String key);
}
