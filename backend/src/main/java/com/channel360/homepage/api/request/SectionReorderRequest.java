package com.channel360.homepage.api;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record SectionReorderRequest(
    @NotNull List<SectionOrderItem> items
) {
    @Builder
    public record SectionOrderItem(
        @NotNull Long id,
        @NotNull Integer displayOrder
    ) {}
}
