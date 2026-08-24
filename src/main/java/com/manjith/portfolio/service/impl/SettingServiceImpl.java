package com.manjith.portfolio.service.impl;

import com.manjith.portfolio.dto.request.SettingRequestDTO;
import com.manjith.portfolio.dto.response.SettingResponseDTO;
import com.manjith.portfolio.entity.Setting;
import com.manjith.portfolio.exception.BusinessValidationException;
import com.manjith.portfolio.exception.ErrorCode;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.SettingMapper;
import com.manjith.portfolio.repository.SettingRepository;
import com.manjith.portfolio.service.SettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * QUERY COUNT: getPublicSettingsMap/getAllSettingsForAdmin -> 1 query each
 * (flat entity, no relations). upsertSetting -> 1 SELECT (find existing)
 * + 1 INSERT or UPDATE. No N+1 possible on a flat key-value table.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {

    private static final Pattern VALID_KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+$");

    private final SettingRepository settingRepository;
    private final SettingMapper settingMapper;

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getPublicSettingsMap() {
        log.info("API entry: getPublicSettingsMap");
        Map<String, String> result = settingRepository.findAll().stream()
                .collect(Collectors.toMap(Setting::getSettingKey, s -> s.getSettingValue() == null ? "" : s.getSettingValue()));
        log.info("API exit: getPublicSettingsMap returning {} keys", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettingResponseDTO> getAllSettingsForAdmin() {
        log.info("API entry: getAllSettingsForAdmin");
        List<SettingResponseDTO> response = settingRepository.findAll().stream()
                .map(settingMapper::toResponseDTO)
                .collect(Collectors.toList());
        log.info("API exit: getAllSettingsForAdmin returning {} settings", response.size());
        return response;
    }

    @Override
    @Transactional
    public SettingResponseDTO upsertSetting(String key, SettingRequestDTO requestDTO) {
        log.info("API entry: upsertSetting key={}", key);

        if (!VALID_KEY_PATTERN.matcher(key).matches()) {
            log.warn("Rejected invalid setting key={}", key);
            throw new BusinessValidationException(ErrorCode.SETTING_INVALID_KEY, ErrorCode.SETTING_INVALID_KEY.getDefaultMessage());
        }

        Setting setting = settingRepository.findBySettingKey(key)
                .orElseGet(() -> Setting.builder().settingKey(key).build());
        setting.setSettingValue(requestDTO.getValue());

        Setting saved = settingRepository.save(setting);
        log.debug("Upserted setting key={}", key);

        log.info("API exit: upsertSetting saved key={}", key);
        return settingMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteSetting(String key) {
        log.info("API entry: deleteSetting key={}", key);

        Setting setting = settingRepository.findBySettingKey(key)
                .orElseThrow(() -> {
                    log.warn("Setting not found for delete, key={}", key);
                    return new ResourceNotFoundException(ErrorCode.SETTING_NOT_FOUND, "No setting found with key: " + key);
                });

        settingRepository.delete(setting);
        log.info("API exit: deleteSetting deleted key={}", key);
    }
}
