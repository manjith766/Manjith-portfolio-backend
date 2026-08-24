package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.SettingResponseDTO;
import com.manjith.portfolio.entity.Setting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SettingMapper {

    @Mapping(target = "key", source = "settingKey")
    @Mapping(target = "value", source = "settingValue")
    SettingResponseDTO toResponseDTO(Setting setting);
}
