package com.channel360.workflow.application.engine.condition.operator;

import org.springframework.stereotype.Component;

@Component
public class EqOperator implements Operator {
    @Override
    public String type() { return "eq"; }

    @Override
    public boolean evaluate(Object fieldValue, Object expectedValue) {
        if (fieldValue == null || expectedValue == null) return false;
        return fieldValue.toString().equals(expectedValue.toString());
    }
}
