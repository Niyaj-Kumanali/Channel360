package com.channel360.workflow.application.engine.condition.operator;

import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InOperator implements Operator {
    @Override
    public String type() { return "in"; }

    @Override
    public boolean evaluate(Object fieldValue, Object expectedValue) {
        if (fieldValue == null || expectedValue == null) return false;
        Set<String> values = Arrays.stream(expectedValue.toString().split(","))
            .map(String::trim).collect(Collectors.toSet());
        return values.contains(fieldValue.toString());
    }
}
