package com.channel360.workflow.application.engine.condition.evaluator;

import com.channel360.workflow.domain.enums.ConditionType;
import com.channel360.workflow.domain.valueobject.BusinessContext;

public interface ConditionEvaluator {
    ConditionType type();
    boolean evaluate(BusinessContext ctx, String field, String op, String value);
}
