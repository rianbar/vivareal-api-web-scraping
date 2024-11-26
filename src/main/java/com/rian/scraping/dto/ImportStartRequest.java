package com.rian.scraping.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record ImportStartRequest(@NotBlank String key) {
}
