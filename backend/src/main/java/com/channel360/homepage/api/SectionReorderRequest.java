package com.channel360.homepage.api;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SectionReorderRequest {
    @NotNull
    private List<SectionOrderItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionOrderItem {
        @NotNull
        private Long id;
        @NotNull
        private Integer displayOrder;
    }
}
