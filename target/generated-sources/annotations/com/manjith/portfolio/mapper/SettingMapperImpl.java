package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.SettingResponseDTO;
import com.manjith.portfolio.entity.Setting;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T17:19:48+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class SettingMapperImpl implements SettingMapper {

    @Override
    public SettingResponseDTO toResponseDTO(Setting setting) {
        if ( setting == null ) {
            return null;
        }

        SettingResponseDTO.SettingResponseDTOBuilder settingResponseDTO = SettingResponseDTO.builder();

        settingResponseDTO.key( setting.getSettingKey() );
        settingResponseDTO.value( setting.getSettingValue() );
        settingResponseDTO.id( setting.getId() );
        settingResponseDTO.updatedAt( setting.getUpdatedAt() );

        return settingResponseDTO.build();
    }
}
