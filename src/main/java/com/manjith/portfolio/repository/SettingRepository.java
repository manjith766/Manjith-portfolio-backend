package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettingRepository extends JpaRepository<Setting, Long> {

    Optional<Setting> findBySettingKey(String settingKey);

    void deleteBySettingKey(String settingKey);
}
