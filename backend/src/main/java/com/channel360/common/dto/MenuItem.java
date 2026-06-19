package com.channel360.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    private String path;
    private String label;
    private String icon;
    private List<String> roles;
    private List<MenuItem> children;
}
