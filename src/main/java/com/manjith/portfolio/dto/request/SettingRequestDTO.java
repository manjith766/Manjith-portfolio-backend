package com.manjith.portfolio.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Note: settingKey is NOT part of this DTO — it comes from the path
 * variable on PUT /api/admin/settings/{key} (upsert-by-key), so the key
 * in the URL is always the key that gets written, with no risk of a body
 * field disagreeing with the path.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingRequestDTO {

    @NotNull(message = "value must be provided (use an empty string, not null, to clear a setting)")
    @Size(max = 10000, message = "value must not exceed 10000 characters")
    private String value;
}
