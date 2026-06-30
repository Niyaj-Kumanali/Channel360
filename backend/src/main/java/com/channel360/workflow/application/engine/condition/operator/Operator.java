package com.channel360.workflow.application.engine.condition.operator;

public interface Operator {
    String type();
    boolean evaluate(Object fieldValue, Object expectedValue);
}
