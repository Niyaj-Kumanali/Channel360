package com.channel360.workflow.domain.model;

import com.channel360.workflow.domain.valueobject.BusinessContext;
import java.util.function.Predicate;

public record CompiledCondition(Predicate<BusinessContext> predicate) {
    public boolean evaluate(BusinessContext ctx) {
        return predicate.test(ctx);
    }

    public static CompiledCondition alwaysTrue() {
        return new CompiledCondition(ctx -> true);
    }

    public static CompiledCondition alwaysFalse() {
        return new CompiledCondition(ctx -> false);
    }
}
