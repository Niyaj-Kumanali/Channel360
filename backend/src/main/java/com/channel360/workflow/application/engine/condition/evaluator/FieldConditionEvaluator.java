package com.channel360.workflow.application.engine.condition.evaluator;

import com.channel360.workflow.application.engine.condition.operator.Operator;
import com.channel360.workflow.domain.enums.ConditionType;
import com.channel360.workflow.domain.valueobject.BusinessContext;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FieldConditionEvaluator implements ConditionEvaluator {

    private final Map<String, Operator> operators;

    public FieldConditionEvaluator(List<Operator> operatorList) {
        this.operators = operatorList.stream()
            .collect(Collectors.toMap(Operator::type, Function.identity()));
    }

    @Override
    public ConditionType type() { return ConditionType.LEAF; }

    @Override
    public boolean evaluate(BusinessContext ctx, String field, String op, String value) {
        Operator operator = operators.get(op);
        if (operator == null) return false;
        Object fieldValue = ctx.getRaw(field);
        return operator.evaluate(fieldValue, value);
    }
}
