package com.channel360.common.response;

import java.util.List;

public record PaginatedResult<T>(List<T> data, long totalCount) {
}
