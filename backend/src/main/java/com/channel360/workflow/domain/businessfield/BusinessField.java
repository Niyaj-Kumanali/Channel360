package com.channel360.workflow.domain.businessfield;

public record BusinessField<T>(String name, Class<T> type) {
}
