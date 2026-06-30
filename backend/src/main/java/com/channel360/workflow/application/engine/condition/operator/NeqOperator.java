package com.channel360.workflow.application.engine.condition.operator;

import org.springframework.stereotype.Component;

@Component
public class NeqOperator implements Operator {
    @Override
    public String type() { return "neq"; }

    @Override
    public boolean evaluate(Object fieldValue, Object expectedValue) {
        if (fieldValue == null || expectedValue == null) return true;
        return !fieldValue.toString().equals(expectedValue.toString());
    }
}
